package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
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
                .sorted(Comparator.comparing(ActivityDto::getName, String.CASE_INSENSITIVE_ORDER))
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

        /*for (ThemeDto themeDto : activityDto.getThemes()) {
            Theme theme = themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND));
            if (theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED);
            }
        }*/

        Activity activity = new Activity(activityDto.getName(), activityDto.getRegion(), Activity.State.APPROVED);
        for (ThemeDto themeDto : activityDto.getThemes()) {
            Theme theme = themeRepository.findById(themeDto.getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND));
            activity.addTheme(theme);
            theme.addActivity(activity);
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addTheme(Integer activityId, List<Theme> themes) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        if (themes.isEmpty()) {
            throw new HEException(EMPTY_THEME_LIST);
        }

        /*for (Theme theme : themes) {
            if (theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED);
            }
        }*/

        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));
        for (Theme theme : themes) {
            activity.addTheme(theme);
            theme.addActivity(activity);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeTheme(Integer activityId, List<Theme> themes) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        }
        if (themes.isEmpty()) {
            throw new HEException(EMPTY_THEME_LIST);
        }

        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));
        for (int i=0; themes.size()<i; i++) {
            activity.removeTheme(themes.get(i).getId());
            themes.get(i).removeActivity(activity.getId());
        }
    }
}