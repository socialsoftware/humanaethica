package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

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

        for (Activity act : activityRepository.findAll()) {
            if (act.getName().equals(activityDto.getName())) {
                throw new HEException(ACTIVITY_ALREADY_EXISTS);
            }
        }

        Activity activity = new Activity(activityDto.getName(), activityDto.getRegion(), activityDto.getThemes(), Activity.State.SUBMITTED);
        for (Theme theme : activityDto.getThemes()) {
            theme.addActivity(activity);
        }
        activityRepository.save(activity);
        return activity;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivityDto> suspendActivity(Integer activityId) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));

        activity.suspend();
        return getActivities();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void validateActivity(Integer activityId) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));
        List<Theme> themes = activity.getThemes();
        /*for (Theme theme : themes) {
            if (theme != null && theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED);
            }
        }*/
        if (activity.getState() == Activity.State.APPROVED) {
            throw new HEException(ACTIVITY_ALREADY_APPROVED, activity.getName());
        }
        activity.setState(Activity.State.APPROVED);
    }
}