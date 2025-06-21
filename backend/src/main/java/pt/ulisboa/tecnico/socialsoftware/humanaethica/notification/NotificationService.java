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

        List<Notification> notifications = subscribers.stream()
            .map(volunteer -> {
                Notification n = new Notification();
                n.setRecipient(volunteer);
                n.setType(Notification.NotificationType.ACTIVITY_PUBLISHED);
                n.setActivity(activity);
                n.setMessage("A new activity \"" + activity.getName() + "\" was published by " + institution.getName() + "!");
                return n;
            }).toList();

        notificationRepository.saveAll(notifications);
    }

    public void createActivitySuggestionNotifications(Institution institution, ActivitySuggestion activitySuggestion) {
        List<Member> members = userRepository.findMembersByInstitutionId(institution.getId());

        List<Notification> notifications = members.stream()
            .map(member -> {
                Notification n = new Notification();
                n.setRecipient(member);
                n.setType(Notification.NotificationType.ACTIVITY_SUGGESTION_SUBMITTED);
                n.setMessage("A new activity suggestion \"" + activitySuggestion.getName() + "\" was published to your institution!");
                return n;
            }).toList();

        notificationRepository.saveAll(notifications);
    }

    public void editActivitySuggestionNotifications(Institution institution, ActivitySuggestion activitySuggestion) {
        List<Member> members = userRepository.findMembersByInstitutionId(institution.getId());

        List<Notification> notifications = members.stream()
            .map(member -> {
                Notification n = new Notification();
                n.setRecipient(member);
                n.setType(Notification.NotificationType.ACTIVITY_SUGGESTION_UPDATED);
                n.setMessage("The activity suggestion \"" + activitySuggestion.getName() + "\" from your institution was just edited!");
                return n;
            }).toList();

        notificationRepository.saveAll(notifications);
    }

    public void createUpvoteNotifications(ActivitySuggestion activitySuggestion) {
        Volunteer volunteer = userRepository.findVolunteerByActivitySuggestionId(activitySuggestion.getId());

        Notification notification = new Notification();
        notification.setRecipient(volunteer);
        notification.setType(Notification.NotificationType.ACTIVITY_SUGGESTION_UPVOTED);
        notification.setMessage("Your activity suggestion \"" + activitySuggestion.getName() + "\" has just received an upvote!");

        notificationRepository.save(notification);
    }

    public void createDownvoteNotifications(ActivitySuggestion activitySuggestion) {
        Volunteer volunteer = userRepository.findVolunteerByActivitySuggestionId(activitySuggestion.getId());

        Notification notification = new Notification();
        notification.setRecipient(volunteer);
        notification.setType(Notification.NotificationType.ACTIVITY_SUGGESTION_DOWNVOTED);
        notification.setMessage("Your activity suggestion \"" + activitySuggestion.getName() + "\" has just received a downvote!");

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