package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

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
        Member member = (Member) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        Institution institution = member.getInstitution();

        List<Theme> themes = activityDto.getThemes().stream()
                .map(themeDto -> themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND)))
                .toList();

        Activity activity = new Activity(activityDto, institution, themes);

        activityRepository.save(activity);

        return new ActivityDto(activity, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto updateActivity(Integer activityId, ActivityDto activityDto) {
        List<Theme> themeList = activityDto.getThemes().stream()
                .map(themeDto -> themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND)))
                .collect(Collectors.toList());

        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityDto.getId()));

        activity.update(activityDto, themeList);

        return new ActivityDto(activity, false);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto suspendActivity(Integer activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));

        activity.suspend();

        return new ActivityDto(activity, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto reportActivity(Integer activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));

        activity.report();

        return new ActivityDto(activity, false);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivityDto validateActivity(Integer activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        activity.validate();

        return new ActivityDto(activity, true);
    }

}