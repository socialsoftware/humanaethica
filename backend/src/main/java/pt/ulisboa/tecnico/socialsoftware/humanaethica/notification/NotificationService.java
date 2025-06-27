package pt.ulisboa.tecnico.socialsoftware.humanaethica.notification;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.domain.Notification;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.repository.NotificationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    public void createActivityNotifications(Institution institution, Activity activity) {
        List<Volunteer> subscribers = userRepository.findVolunteersSubscribedToInstitution(institution.getId());
        String message = Notification.NotificationType.ACTIVITY_PUBLISHED.formatMessage(activity.getName(), institution.getName());

        List<Notification> notifications = subscribers.stream()
            .map(volunteer -> {
                return new Notification(message, volunteer);
            }).toList();

        notificationRepository.saveAll(notifications);
    }

    public void createActivitySuggestionNotifications(Institution institution, ActivitySuggestion activitySuggestion) {
        List<Member> members = userRepository.findMembersByInstitutionId(institution.getId());
        String message = Notification.NotificationType.ACTIVITY_SUGGESTION_SUBMITTED.formatMessage(activitySuggestion.getName());

        List<Notification> notifications = members.stream()
            .map(member -> {
                return new Notification(message, member);
            }).toList();

        notificationRepository.saveAll(notifications);
    }

    public void editActivitySuggestionNotifications(Institution institution, ActivitySuggestion activitySuggestion) {
        List<Member> members = userRepository.findMembersByInstitutionId(institution.getId());
        String message = Notification.NotificationType.ACTIVITY_SUGGESTION_UPDATED.formatMessage(activitySuggestion.getName());

        List<Notification> notifications = members.stream()
            .map(member -> {
                return new Notification(message, member);
            }).toList();

        notificationRepository.saveAll(notifications);
    }

    public void createUpvoteNotifications(ActivitySuggestion activitySuggestion) {
        Volunteer volunteer = userRepository.findVolunteerByActivitySuggestionId(activitySuggestion.getId());
        String message = Notification.NotificationType.ACTIVITY_SUGGESTION_UPVOTED.formatMessage(activitySuggestion.getName());
        Notification notification = new Notification(message, volunteer);
        notificationRepository.save(notification);
    }

    public void createDownvoteNotifications(ActivitySuggestion activitySuggestion) {
        Volunteer volunteer = userRepository.findVolunteerByActivitySuggestionId(activitySuggestion.getId());
        String message = Notification.NotificationType.ACTIVITY_SUGGESTION_DOWNVOTED.formatMessage(activitySuggestion.getName());
        Notification notification = new Notification(message, volunteer);
        notificationRepository.save(notification);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void markAllAsRead(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }
}