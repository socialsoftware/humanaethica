package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ThemeRepository themeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstitutionRepository institutionRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivityDto> getActivities() {
        return activityRepository.findAll().stream()
                .map(activity-> new ActivityDto(activity,true))
                .sorted(Comparator.comparing(ActivityDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto registerActivity(Integer userId, ActivityDto activityDto) {

        if (userId == null) {
            throw new HEException(USER_NOT_FOUND);
        }

        if (activityDto.getName() == null || activityDto.getName().trim().length() == 0) {
            throw new HEException(INVALID_ACTIVITY_NAME, activityDto.getName());
        }

        if (activityDto.getRegion() == null || activityDto.getRegion().trim().length() == 0) {
            throw new HEException(INVALID_REGION_NAME, activityDto.getRegion());
        }

        if (activityDto.getDescription() == null || activityDto.getDescription().trim().length() == 0) {
            throw new HEException(INVALID_DESCRIPTION);
        }

        if (activityDto.getStartingDate() == null || !DateHandler.isValidDateFormat(activityDto.getStartingDate())) {
            throw new HEException(INVALID_DATE);
        }

        if (activityDto.getEndingDate() == null || !DateHandler.isValidDateFormat(activityDto.getEndingDate())) {
            throw new HEException(INVALID_DATE);
        }

        for (Activity act : activityRepository.findAll()) {
            if (act.getName().equals(activityDto.getName())) {
                throw new HEException(ACTIVITY_ALREADY_EXISTS);
            }
        }

        for (ThemeDto themeDto : activityDto.getThemes()) {
            Theme theme = themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND));
            if (theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED);
            }
        }

        Member member = (Member) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        Activity activity = new Activity(activityDto.getName(), activityDto.getRegion(), activityDto.getDescription(), member.getInstitution(),
                activityDto.getStartingDate(), activityDto.getEndingDate(), Activity.State.APPROVED);

        for (ThemeDto themeDto : activityDto.getThemes()) {
            Theme theme = themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND));
            activity.addTheme(theme);
        }

        activityRepository.save(activity);
        return new ActivityDto(activity, false);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto suspendActivity(Integer activityId) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));
        if (activity.getState() == Activity.State.SUSPENDED) {
            throw new HEException(ACTIVITY_ALREADY_SUSPENDED, activity.getName());
        }
        activity.suspend();

        return new ActivityDto(activity, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto reportActivity(Integer activityId) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));
        if (activity.getState() == Activity.State.REPORTED) {
            throw new HEException(ACTIVITY_ALREADY_REPORTED, activity.getName());
        }
        activity.setState(Activity.State.REPORTED);

        return new ActivityDto(activity, false);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto validateActivity(Integer activityId) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));
        List<Theme> themes = activity.getThemes();
        for (Theme theme : themes) {
            if (theme != null && theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED);
            }
        }
        if (activity.getState() == Activity.State.APPROVED) {
            throw new HEException(ACTIVITY_ALREADY_APPROVED, activity.getName());
        }
        activity.setState(Activity.State.APPROVED);

        return new ActivityDto(activity, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto updateActivity(Integer userId, Integer activityId, ActivityDto activityDto) {

        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }

        if (userId == null) {
            throw new HEException(USER_NOT_FOUND);
        }

        if (activityDto.getName() == null || activityDto.getName().trim().length() == 0) {
            throw new HEException(INVALID_ACTIVITY_NAME, activityDto.getName());
        }

        if (activityDto.getRegion() == null || activityDto.getRegion().trim().length() == 0) {
            throw new HEException(INVALID_REGION_NAME, activityDto.getRegion());
        }

        if (activityDto.getDescription() == null || activityDto.getDescription().trim().length() == 0) {
            throw new HEException(INVALID_DESCRIPTION);
        }

        if (activityDto.getStartingDate() == null || !DateHandler.isValidDateFormat(activityDto.getStartingDate())) {
            throw new HEException(INVALID_DATE);
        }

        if (activityDto.getEndingDate() == null || !DateHandler.isValidDateFormat(activityDto.getEndingDate())) {
            throw new HEException(INVALID_DATE);
        }

        for (ThemeDto themeDto : activityDto.getThemes()) {
            Theme theme = themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND));
            if (theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED);
            }
        }

        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityDto.getId()));
        activity.setName(activityDto.getName());
        activity.setRegion(activityDto.getRegion());
        activity.setDescription(activityDto.getDescription());
        activity.setStartingDate(activityDto.getStartingDate());
        activity.setEndingDate(activityDto.getEndingDate());

        List<Theme> themeList = activityDto.getThemes().stream()
                .map(themeDto -> themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND)))
                .collect(Collectors.toList());

        activity.setThemes(themeList);


        User user = userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        if (user.getRole() != User.Role.MEMBER) {
            throw new HEException(INVALID_ROLE, user.getRole().name());
        }

        Member member = (Member) user;
        activity.setInstitution(member.getInstitution());

        activityRepository.save(activity);
        return new ActivityDto(activity, false);
    }
}