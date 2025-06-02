package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.subscriptions;

import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserDeletedEvent;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserRegisteredEvent;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.INVALID_AUTH_USERNAME;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.USERNAME_ALREADY_EXIST;

@Component
public class AuthUserEventListener {


    @Autowired
    private AuthUserRepository authUserRepository;


    @Subscribe
    public void hanldeUserRegistationEvent(UserRegisteredEvent event) {

        RegisterUserDto registerUserDto = event.getRegisterUserDto();

        if (registerUserDto.getUsername() == null || registerUserDto.getUsername().trim().length() == 0) {
            throw new HEException(INVALID_AUTH_USERNAME, registerUserDto.getUsername());
        }

        if (authUserRepository.findAuthUserByUsername(registerUserDto.getUsername()).isPresent()) {
            throw new HEException(USERNAME_ALREADY_EXIST, (event.getRegisterUserDto().getUsername()));
        }


        authUserRepository.save(AuthUser.createAuthUser(event.getUserId(), registerUserDto.getUsername(), registerUserDto.getEmail(), event.getType(), registerUserDto.getRole(), registerUserDto.getName()));
    }

    @Subscribe
    public void onUserDeleted(UserDeletedEvent event) {
        int deletedUserId = event.getUserId();
        AuthUser authUser = authUserRepository.findAuthUserByUserId(deletedUserId)
                .orElse(null);
        if (authUser != null) {
            authUser.setActive(false);
            authUserRepository.save(authUser);
        }
    }

}
