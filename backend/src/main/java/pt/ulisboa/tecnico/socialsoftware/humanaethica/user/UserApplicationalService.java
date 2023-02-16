package pt.ulisboa.tecnico.socialsoftware.humanaethica.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.UserDocument;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.LinkHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer;

@Service
public class UserApplicationalService {
    @Autowired
    private UserService userService;

    @Autowired
    private Mailer mailer;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void setInstitutionFromMember(RegisterUserDto userDto, int memberId){
        AuthUser member = authUserRepository.findById(memberId).orElseThrow(() -> new HEException(ErrorMessage.USER_NOT_FOUND));
        userDto.setInstitutionId(((Member) member.getUser()).getInstitution().getId());
    }

    public int getInstitutionId(int memberId){
        AuthUser user = authUserRepository.findById(memberId).orElseThrow(() -> new HEException(ErrorMessage.USER_NOT_FOUND));
        return ((Member) user.getUser()).getInstitution().getId();
    }

    public UserDocument getUserDocument(Integer id) {
        return userService.getDoc(id);
    }

    public RegisterUserDto confirmRegistration(RegisterUserDto registerUserDto) {
        RegisterUserDto user = userService.confirmRegistrationTransactional(registerUserDto);
        if (!user.isActive()) {
            sendConfirmationEmailTo(user.getUsername(), user.getEmail(), user.getConfirmationToken());
        }
        return user;
    }

    public void validateUser(Integer userId) {
        AuthNormalUser authUser = (AuthNormalUser) authUserRepository.findById(userId).orElseThrow(() -> new HEException(ErrorMessage.AUTHUSER_NOT_FOUND));
        if (!authUser.isActive() && !authUser.getUser().getState().equals(User.State.ACTIVE)){
            userService.validateUser(authUser);
            if (!authUser.getUser().getRole().equals(User.Role.MEMBER) || ((Member) authUser.getUser()).getInstitution().isActive())
                sendConfirmationEmailTo(authUser.getUsername(), authUser.getEmail(), authUser.getConfirmationToken());
        }
    }

    public void sendConfirmationEmailTo(String username, String email, String token) {
        mailer.sendSimpleMail(mailUsername, email, Mailer.QUIZZES_TUTOR_SUBJECT + UserService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, buildMailBody(username, token));
    }

    private String buildMailBody(String username, String token) {
        String msg = "To confirm your registration, as external user using username (" + username + ") click the following link";
        return String.format("%s: %s", msg, LinkHandler.createConfirmRegistrationLink(username, token));
    }
}
