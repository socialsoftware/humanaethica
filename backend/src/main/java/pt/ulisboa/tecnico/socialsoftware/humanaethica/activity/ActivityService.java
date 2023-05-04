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
    public Activity registerActivity(ActivityDto activityDto) {

        if (activityDto.getName() == null || activityDto.getName().trim().length() == 0) {
            throw new HEException(INVALID_ACTIVITY_NAME, activityDto.getName());
        }

        if (activityDto.getRegion() == null || activityDto.getRegion().trim().length() == 0) {
            throw new HEException(INVALID_REGION_NAME, activityDto.getRegion());
        }

        if (activityDto.getTheme() == null || activityDto.getTheme().getName().length() == 0) {
            throw new HEException(INVALID_THEME_NAME, activityDto.getTheme().getName());
        }

        for (Activity act : activityRepository.findAll()) {
            if (act.getName().equals(activityDto.getName())) {
                throw new HEException(ACTIVITY_ALREADY_EXISTS);
            }
        }

        Theme existentTheme = null;
        Activity activity = null;

        for (Theme theme : themeRepository.findAll()) {
            if (theme.getName().equals(activityDto.getTheme().getName())) {
                existentTheme = theme;
            }
        }
        if (existentTheme == null) {
            Theme theme = new Theme(activityDto.getTheme().getName());
            themeRepository.save(theme);
            activity = new Activity(activityDto.getName(), activityDto.getRegion(), theme);
            theme.addActivity(activity);
            activityRepository.save(activity);
        }
        else {
            activity = new Activity(activityDto.getName(), activityDto.getRegion(), existentTheme);
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