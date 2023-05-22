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

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
                .map(activity->new ActivityDto(activity,false))
                .sorted(Comparator.comparing(ActivityDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Activity registerActivity(Integer userId, ActivityDto activityDto) {

        if (userId == null) {
            throw new HEException(USER_NOT_FOUND);
        }

        if (activityDto.getName() == null || activityDto.getName().trim().length() == 0) {
            throw new HEException(INVALID_ACTIVITY_NAME, activityDto.getName());
        }

        if (activityDto.getRegion() == null || activityDto.getRegion().trim().length() == 0) {
            throw new HEException(INVALID_REGION_NAME, activityDto.getRegion());
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

        Activity activity = new Activity(activityDto.getName(), activityDto.getRegion(), Activity.State.APPROVED);
        for (ThemeDto themeDto : activityDto.getThemes()) {
            Theme theme = themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND));
            activity.addTheme(theme);
        }

        if(userId == -1) {
            Institution institution = institutionRepository.findById(activityDto.getInstitution().getId()).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));
            activity.setInstitution(institution);
        }

        else{
            User user = userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
            if (user.getRole() != User.Role.MEMBER) {
                throw new HEException(INVALID_ROLE, user.getRole().name());
            }

            Member member = (Member) user;
            activity.setInstitution(member.getInstitution());
        }

        activityRepository.save(activity);
        return activity;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void suspendActivity(Integer activityId) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));
        if (activity.getState() == Activity.State.SUSPENDED) {
            throw new HEException(ACTIVITY_ALREADY_SUSPENDED, activity.getName());
        }
        activity.suspend();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void reportActivity(Integer activityId) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));
        if (activity.getState() == Activity.State.REPORTED) {
            throw new HEException(ACTIVITY_ALREADY_REPORTED, activity.getName());
        }
        activity.setState(Activity.State.REPORTED);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void validateActivity(Integer activityId) {
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
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Activity updateActivity(Integer userId, Integer activityId, ActivityDto activityDto) {

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

        for (ThemeDto themeDto : activityDto.getThemes()) {
            Theme theme = themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND));
            if (theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED);
            }
        }

        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityDto.getId()));
        activity.setName(activityDto.getName());
        activity.setRegion(activityDto.getRegion());
        List<Theme> themeList = new ArrayList<>();

        for (ThemeDto themeDto : activityDto.getThemes()) {
            Theme theme = themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND));
            themeList.add(theme);
            if(!activity.getThemes().contains(theme)) {
                theme.addActivity(activity);
            }
        }

        for (Theme theme : activity.getThemes()) {
            if(!themeList.contains(theme)) {
                theme.removeActivity(activityId);
            }
        }

        activity.setThemes(themeList);


        if(userId == -1) {
            Institution institution = institutionRepository.findById(activityDto.getInstitution().getId()).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));
            activity.setInstitution(institution);
        }

        else{
            User user = userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
            if (user.getRole() != User.Role.MEMBER) {
                throw new HEException(INVALID_ROLE, user.getRole().name());
            }

            Member member = (Member) user;
            activity.setInstitution(member.getInstitution());
        }

        activityRepository.save(activity);
        return activity;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void subscribeActivity(Integer userId, Integer activityId) {
        if (userId == null) {
            throw new HEException(USER_NOT_FOUND);
        }
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        if (user.getRole() != User.Role.VOLUNTEER) {
            throw new HEException(INVALID_ROLE, user.getRole().name());
        }

        Volunteer volunteer = (Volunteer) user;
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));;

        activity.addVolunteer(volunteer);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unsubscribeActivity(Integer userId, Integer activityId) {
        if (userId == null) {
            throw new HEException(USER_NOT_FOUND);
        }
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        if (user.getRole() != User.Role.VOLUNTEER) {
            throw new HEException(INVALID_ROLE, user.getRole().name());
        }

        Volunteer volunteer = (Volunteer) user;
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));;

        activity.removeVolunteer(volunteer.getId());
    }
}