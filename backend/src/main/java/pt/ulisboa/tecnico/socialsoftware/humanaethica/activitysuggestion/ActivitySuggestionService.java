package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.repository.ActivitySuggestionRepository;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_INVALID;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INSTITUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.USER_NOT_FOUND;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.NotificationService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

@Service
public class ActivitySuggestionService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    ActivitySuggestionRepository activitySuggestionRepository;
    @Autowired
    private NotificationService notificationService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivitySuggestionDto> getAllActivitySuggestions() {
        List<ActivitySuggestion> activitySuggestions = activitySuggestionRepository.findAll();

        return activitySuggestions.stream()
            .map(activitySuggestion -> new ActivitySuggestionDto(activitySuggestion, true))
            .sorted(Comparator.comparing(ActivitySuggestionDto::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivitySuggestionDto> getActivitySuggestionsByInstitution(Integer institutionId) {
        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        return this.activitySuggestionRepository.getActivitySuggestionsByInstitutionId(institutionId).stream()
                .map(activitySuggestion-> new ActivitySuggestionDto(activitySuggestion, true))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivitySuggestionDto> getActivitySuggestionsByVolunteer(Integer userId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        return this.activitySuggestionRepository.getActivitySuggestionsByVolunteerId(userId).stream()
                .map(activitySuggestion-> new ActivitySuggestionDto(activitySuggestion, true))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto createActivitySuggestion(Integer userId, Integer institutionId, ActivitySuggestionDto activitySuggestionDto) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));

        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        Institution institution =  institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        if (activitySuggestionDto == null) throw new HEException(ACTIVITY_SUGGESTION_DESCRIPTION_INVALID);

        ActivitySuggestion activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto);
        activitySuggestionRepository.save(activitySuggestion);

        notificationService.createActivitySuggestionNotifications(institution, activitySuggestion);

        return new ActivitySuggestionDto(activitySuggestion, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto updateActivitySuggestion(Integer activitySuggestionId, ActivitySuggestionDto activitySuggestionDto,  Integer institutionId) {
        if (activitySuggestionId == null) throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        ActivitySuggestion activitySuggestion = activitySuggestionRepository.findById(activitySuggestionId).orElseThrow(() -> new HEException(ACTIVITY_SUGGESTION_NOT_FOUND, activitySuggestionId));

        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        Institution institution =  institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        activitySuggestion.update(activitySuggestionDto, institution);
        notificationService.editActivitySuggestionNotifications(institution, activitySuggestion);

        return new ActivitySuggestionDto(activitySuggestion, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto approveActivitySuggestion(Integer activitySuggestionId) {
        if (activitySuggestionId == null) throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        ActivitySuggestion activitySuggestion = activitySuggestionRepository.findById(activitySuggestionId)
                                                                  .orElseThrow(() -> new HEException(ACTIVITY_SUGGESTION_NOT_FOUND, activitySuggestionId));

        activitySuggestion.approve();

        return new ActivitySuggestionDto(activitySuggestion, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto rejectActivitySuggestion(Integer activitySuggestionId, String justification) {
        if (activitySuggestionId == null) throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        ActivitySuggestion activitySuggestion = activitySuggestionRepository.findById(activitySuggestionId)
                                                                  .orElseThrow(() -> new HEException(ACTIVITY_SUGGESTION_NOT_FOUND, activitySuggestionId));
        activitySuggestion.reject(justification);

        return new ActivitySuggestionDto(activitySuggestion, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto upvoteActivitySuggestion(Integer userId, Integer activitySuggestionId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));
        
        if (activitySuggestionId == null) throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        ActivitySuggestion activitySuggestion = activitySuggestionRepository.findById(activitySuggestionId)
                                                                  .orElseThrow(() -> new HEException(ACTIVITY_SUGGESTION_NOT_FOUND, activitySuggestionId));

        volunteer.addUpvoteActivitySuggestion(activitySuggestion);
        notificationService.createUpvoteNotifications(activitySuggestion);

        return new ActivitySuggestionDto(activitySuggestion, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto removeUpvoteActivitySuggestion(Integer userId, Integer activitySuggestionId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));
        
        if (activitySuggestionId == null) throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        ActivitySuggestion activitySuggestion = activitySuggestionRepository.findById(activitySuggestionId)
                                                                  .orElseThrow(() -> new HEException(ACTIVITY_SUGGESTION_NOT_FOUND, activitySuggestionId));

        volunteer.removeUpvoteActivitySuggestion(activitySuggestion);
        return new ActivitySuggestionDto(activitySuggestion, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto downvoteActivitySuggestion(Integer userId, Integer activitySuggestionId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));
        
        if (activitySuggestionId == null) throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        ActivitySuggestion activitySuggestion = activitySuggestionRepository.findById(activitySuggestionId)
                                                                  .orElseThrow(() -> new HEException(ACTIVITY_SUGGESTION_NOT_FOUND, activitySuggestionId));

        volunteer.addDownvoteActivitySuggestion(activitySuggestion);
        notificationService.createUpvoteNotifications(activitySuggestion);
        return new ActivitySuggestionDto(activitySuggestion, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto removeDownvoteActivitySuggestion(Integer userId, Integer activitySuggestionId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));
        
        if (activitySuggestionId == null) throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        ActivitySuggestion activitySuggestion = activitySuggestionRepository.findById(activitySuggestionId)
                                                                  .orElseThrow(() -> new HEException(ACTIVITY_SUGGESTION_NOT_FOUND, activitySuggestionId));

        volunteer.removeDownvoteActivitySuggestion(activitySuggestion);
        return new ActivitySuggestionDto(activitySuggestion, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivitySuggestionDto> getUpvotedActivitySuggestions(Integer userId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));

        Map<Integer, Boolean> votesMap = volunteer.getActivitySuggestionVotes();

        List<Integer> votedIds = votesMap.entrySet().stream()
                                         .filter(Map.Entry::getValue) // only entries with true (ipvotes)
                                         .map(Map.Entry::getKey)
                                         .collect(Collectors.toList());

        List<ActivitySuggestion> activitySuggestions = activitySuggestionRepository.findAllById(votedIds);

        return activitySuggestions.stream()
            .map(activitySuggestion -> new ActivitySuggestionDto(activitySuggestion, true))
            .sorted(Comparator.comparing(ActivitySuggestionDto::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivitySuggestionDto> getDownvotedActivitySuggestions(Integer userId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));

        Map<Integer, Boolean> votesMap = volunteer.getActivitySuggestionVotes();

        List<Integer> votedIds = votesMap.entrySet().stream()
                                         .filter(entry -> !entry.getValue()) // only entries with false (downvotes)
                                         .map(Map.Entry::getKey)
                                         .collect(Collectors.toList());

        List<ActivitySuggestion> activitySuggestions = activitySuggestionRepository.findAllById(votedIds);

        return activitySuggestions.stream()
            .map(activitySuggestion -> new ActivitySuggestionDto(activitySuggestion, true))
            .sorted(Comparator.comparing(ActivitySuggestionDto::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

}