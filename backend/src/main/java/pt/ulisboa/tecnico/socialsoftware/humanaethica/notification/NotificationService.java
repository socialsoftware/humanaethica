package pt.ulisboa.tecnico.socialsoftware.humanaethica.notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.domain.Notification;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.repository.NotificationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
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
                n.setMessage("A new activity \"" + activity.getName() + "\" was published by " + institution.getName());
                return n;
            }).toList();

        notificationRepository.saveAll(notifications);
    }
}