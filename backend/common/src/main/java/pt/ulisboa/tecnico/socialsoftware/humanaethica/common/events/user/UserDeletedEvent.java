package pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user;

public class UserDeletedEvent {
    private final Integer userId;

    public UserDeletedEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
