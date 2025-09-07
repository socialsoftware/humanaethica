package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserDeletedEvent;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserRegisteredEvent;

@Service
public class UserEventPublisher {

    private final StreamBridge streamBridge;

    public UserEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishUserDeleted(UserDeletedEvent event) {
        streamBridge.send("userDeleted-out-0", event);
    }


    public void publishUserRegistered(UserRegisteredEvent event) {
        streamBridge.send("userRegistered-out-0", event);
    }
}
