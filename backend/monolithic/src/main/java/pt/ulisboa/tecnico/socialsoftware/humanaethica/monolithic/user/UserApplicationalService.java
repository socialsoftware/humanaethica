package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.dto.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.utils.LinkHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.utils.Mailer;

@Service
public class UserApplicationalService {
    @Autowired
    private UserService userService;
    @Autowired
    private Mailer mailer;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public RegisterUserDto confirmRegistration(RegisterUserDto registerUserDto) {
        RegisterUserDto user = userService.confirmRegistration(registerUserDto);
        if (!user.isActive()) {
            sendConfirmationEmailTo(user.getUsername(), user.getEmail(), user.getConfirmationToken());
        }
        return user;
    }

    public void validateUser(Integer userId) {
        RegisterUserDto authUser = userService.validateUser(userId);
        if (!authUser.getRole().equals(User.Role.MEMBER) || authUser.isInstitutionActive()) {
            sendConfirmationEmailTo(authUser.getUsername(), authUser.getEmail(), authUser.getConfirmationToken());
        }
    }

    public void sendConfirmationEmailTo(String username, String email, String token) {
        mailer.sendSimpleMail(mailUsername, email, Mailer.HUMANAETHICA_SUBJECT + UserService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, buildMailBody(username, token));
    }

    private String buildMailBody(String username, String token) {
        String msg = "To confirm your registration, as external user using username (" + username + ") click the following link";
        return String.format("%s: %s", msg, LinkHandler.createConfirmRegistrationLink(username, token));
    }
}
