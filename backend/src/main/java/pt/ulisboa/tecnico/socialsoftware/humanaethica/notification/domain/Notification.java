package pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

@Entity
@Table(name = "notifications")
public class Notification {

    public enum NotificationType {
        ACTIVITY_PUBLISHED,
        ACTIVITY_SUGGESTION_SUBMITTED,
        ACTIVITY_SUGGESTION_UPDATED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String message;
    private boolean read = false;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    private Notification.NotificationType type;

    @ManyToOne
    private User user;

    // Optional links to related entities (only one will be non-null)
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    private ActivitySuggestion activitySuggestion;
    

    public Notification() {
    }

    public Notification(String message, NotificationType type, User user) {
        setMessage(message);
        setCreationDate(DateHandler.now());
        setType(type);
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
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

    public Activity getActivity() {
        if (type != NotificationType.ACTIVITY_PUBLISHED) return null;
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ActivitySuggestion getActivitySuggestion() {
        if (type == NotificationType.ACTIVITY_PUBLISHED) return null;
        return activitySuggestion;
    }

    public void setActivitySuggestion(ActivitySuggestion activitySuggestion) {
        this.activitySuggestion = activitySuggestion;
    }
}