package pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.domain.Notification;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class NotificationDto {
    private Integer id;
    private String message;
    private String creationDate;
    private boolean read;

    public NotificationDto(){
    }

    public NotificationDto(Notification notification){
        setId(notification.getId());
        setMessage(notification.getMessage());
        setCreationDate(DateHandler.toISOString(notification.getCreationDate()));
        setRead(notification.isRead());
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
