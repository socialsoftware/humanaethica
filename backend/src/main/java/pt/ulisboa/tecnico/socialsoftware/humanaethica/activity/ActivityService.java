package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.RegisterActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.Comparator;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ThemeRepository themeRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivityDto> getActivities() {
        return activityRepository.findAll().stream()
                .map(ActivityDto::new)
                .sorted(Comparator.comparing(ActivityDto::getName))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Activity registerActivity(RegisterActivityDto registerActivityDto) {

        if (registerActivityDto.getActivityName() == null || registerActivityDto.getActivityName().trim().length() == 0) {
            throw new HEException(INVALID_ACTIVITY_NAME, registerActivityDto.getActivityName());
        }

        if (registerActivityDto.getActivityRegion() == null || registerActivityDto.getActivityRegion().trim().length() == 0) {
            throw new HEException(INVALID_REGION_NAME, registerActivityDto.getActivityRegion());
        }

        if (registerActivityDto.getActivityTheme() == null || registerActivityDto.getActivityTheme().trim().length() == 0) {
            throw new HEException(INVALID_THEME_NAME, registerActivityDto.getActivityTheme());
        }

        for (Activity act : activityRepository.findAll()) {
            if (act.getName().equals(registerActivityDto.getActivityName())) {
                throw new HEException(ACTIVITY_ALREADY_EXISTS);
            }
        }

        Theme existentTheme = null;
        Activity activity = null;

        for (Theme theme : themeRepository.findAll()) {
            if (theme.getName().equals(registerActivityDto.getActivityTheme())) {
                existentTheme = theme;
            }
        }
        if (existentTheme == null) {
            Theme theme = new Theme(registerActivityDto.getActivityTheme());
            themeRepository.save(theme);
            activity = new Activity(registerActivityDto.getActivityName(), registerActivityDto.getActivityRegion(), theme);
            theme.addActivity(activity);
            activityRepository.save(activity);
        }
        else {
            activity = new Activity(registerActivityDto.getActivityName(), registerActivityDto.getActivityRegion(), existentTheme);
            existentTheme.addActivity(activity);
            activityRepository.save(activity);
        }
        return activity;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivityDto> deleteActivity(Integer activityId) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));

        activityRepository.delete(activity);
        activity.delete();
        return getActivities();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void validateActivity(int activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));
        Theme theme = activity.getTheme();
        /*if (theme != null && !theme.isActive())
            throw new HEException(THEME_NOT_APPROVED);*/
        if (activity.isActive()) {
            throw new HEException(ACTIVITY_ALREADY_ACTIVE, activity.getName());
        }
        activity.validate();
    }
}