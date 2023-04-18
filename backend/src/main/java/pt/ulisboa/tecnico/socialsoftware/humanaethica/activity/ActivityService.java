package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.Comparator;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivityDto> getActivities() {
        return activityRepository.findAll().stream()
                .map(ActivityDto::new)
                .sorted(Comparator.comparing(ActivityDto::getName))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void registerActivity(ActivityDto activityDto) {

        if (activityDto.getName() == null || activityDto.getName().trim().length() == 0) {
            throw new HEException(INVALID_ACTIVITY_NAME, activityDto.getName());
        }

        if (activityDto.getRegion() == null) {
            throw new HEException(INVALID_REGION_NAME, activityDto.getRegion());
        }

        Activity activity = new Activity(activityDto.getName(), activityDto.getRegion());
        activityRepository.save(activity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivityDto> deleteActivity(int activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND));

        activity.delete();
        return getActivities();
    }
}