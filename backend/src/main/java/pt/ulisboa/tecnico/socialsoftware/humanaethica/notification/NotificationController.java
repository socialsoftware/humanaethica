package pt.ulisboa.tecnico.socialsoftware.humanaethica.notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PutMapping("/{userId}/notifications/read")
    public void markAllAsRead(@PathVariable Integer userId) {
        notificationService.markAllAsRead(userId);
    }
}