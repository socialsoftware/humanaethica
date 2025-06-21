package pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

@Entity
@Table(name = "notifications")
public class Notification {

    public enum NotificationType {
        ACTIVITY_PUBLISHED("A new activity \"%s\" was published by %s!"),
        ACTIVITY_SUGGESTION_SUBMITTED("A new activity suggestion \"%s\" was published to your institution!"),
        ACTIVITY_SUGGESTION_UPDATED("The activity suggestion \"%s\" from your institution was just edited!"),
        ACTIVITY_SUGGESTION_UPVOTED("Your activity suggestion \"%s\" has just received an upvote!"),
        ACTIVITY_SUGGESTION_DOWNVOTED("Your activity suggestion \"%s\" has just received a downvote!");

        private final String label;

        NotificationType(String label) {
            this.label = label;
        }

        public String formatMessage(Object... args) {
            return String.format(label, args);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String message;
    private boolean read = false;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = DateHandler.now();

    @ManyToOne
    private User user;

    public Notification() {
    }

    public Notification(String message, User user) {
        setMessage(message);
        setCreationDate(DateHandler.now());
        setRecipient(user);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public User getRecipient() {
        return user;
    }

    public void setRecipient(User user) {
        this.user = user;
        user.addNotification(this);
    }
}