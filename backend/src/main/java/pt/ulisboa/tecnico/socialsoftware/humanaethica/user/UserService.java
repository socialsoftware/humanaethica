package pt.ulisboa.tecnico.socialsoftware.humanaethica.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.UserDocument;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserDocumentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static final String MAIL_FORMAT = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static final String PASSWORD_CONFIRMATION_MAIL_SUBJECT = "Password Confirmation";

    public static final String PASSWORD_CONFIRMATION_MAIL_BODY = "Link to password confirmation page";


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(user->new UserDto(user))
                .sorted(Comparator.comparing(UserDto::getUsername))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createVolunteerWithAuth(String name, String username, String email, AuthUser.Type type, State state) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new HEException(DUPLICATE_USER, username);
        }

        Volunteer volunteer = new Volunteer(name, username, email, type, state);
        userRepository.save(volunteer);
        return volunteer.getAuthUser();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createMemberWithAuth(String name, String username, String email, AuthUser.Type type, Institution institution, State state) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new HEException(DUPLICATE_USER, username);
        }

        Member member = new Member(name, username, email, type, institution, state);
        userRepository.save(member);
        return member.getAuthUser();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public UserDto registerUser(RegisterUserDto registerUserDto, MultipartFile document) throws IOException {
        AuthNormalUser authUser = createAuthNormalUser(registerUserDto, State.SUBMITTED);

        if (document != null) {
            UserDocument userDocument = new UserDocument();
            userDocument.setName(document.getName());
            userDocument.setContent(document.getBytes());

            authUser.getUser().setDocument(userDocument);
            userDocumentRepository.save(userDocument);
        }

        return new UserDto(authUser);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RegisterUserDto confirmRegistration(RegisterUserDto registerUserDto) {
        AuthNormalUser authUser = (AuthNormalUser) authUserRepository.findAuthUserByUsername(registerUserDto.getUsername()).orElse(null);

        if (authUser == null) {
            throw new HEException(ErrorMessage.USER_NOT_FOUND, registerUserDto.getUsername());
        }

        if (registerUserDto.getPassword() == null || registerUserDto.getPassword().isEmpty()) {
            throw new HEException(INVALID_PASSWORD);
        }

        try {
            authUser.confirmRegistration(passwordEncoder, registerUserDto.getConfirmationToken(),
                    registerUserDto.getPassword());
        } catch (HEException e) {
            if (e.getErrorMessage().equals(ErrorMessage.EXPIRED_CONFIRMATION_TOKEN)) {
                authUser.generateConfirmationToken();
            } else throw new HEException(e.getErrorMessage());
        }

        authUser.getUser().setState(State.ACTIVE);
        return new RegisterUserDto(authUser);
    }


    private AuthNormalUser createAuthNormalUser(RegisterUserDto registerUserDto, State state) {
        if (registerUserDto.getUsername() == null || registerUserDto.getUsername().trim().length() == 0) {
            throw new HEException(INVALID_AUTH_USERNAME, registerUserDto.getUsername());
        }

        if (registerUserDto.getRole() == null) {
            throw new HEException(INVALID_ROLE, "null");
        }

        if (authUserRepository.findAuthUserByUsername(registerUserDto.getUsername()).isPresent()) {
            throw new HEException(USERNAME_ALREADY_EXIST, registerUserDto.getUsername());
        }


        User user = switch (registerUserDto.getRole()) {
            case VOLUNTEER -> new Volunteer(registerUserDto.getName(),
                    registerUserDto.getUsername(), registerUserDto.getEmail(), AuthUser.Type.NORMAL, state);
            case MEMBER -> {
                Institution institution = institutionRepository.findById(registerUserDto.getInstitutionId()).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, registerUserDto.getInstitutionId()));
                yield new Member(registerUserDto.getName(),
                        registerUserDto.getUsername(), registerUserDto.getEmail(), AuthUser.Type.NORMAL, institution, state);
            }
            case ADMIN -> new Admin(registerUserDto.getName(),
                    registerUserDto.getUsername(), registerUserDto.getEmail(), AuthUser.Type.NORMAL, state);
            default -> throw new HEException(INVALID_ROLE, registerUserDto.getRole().name());
        };

        userRepository.save(user);

        return (AuthNormalUser) user.getAuthUser();

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<UserDto> deleteUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));

        Institution i = user.remove();
        if (i != null)
            institutionRepository.save(i);

        return getUsers();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDocument getDocument(Integer id) {
        return (userRepository.findById(id).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND))).getDocument();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RegisterUserDto validateUser(Integer userId) {
        AuthNormalUser authUser = (AuthNormalUser) authUserRepository.findById(userId).orElseThrow(() -> new HEException(ErrorMessage.AUTHUSER_NOT_FOUND));
        if (authUser.isActive() || authUser.getUser().getState().equals(User.State.ACTIVE)){
            throw new HEException(ErrorMessage.USER_ALREADY_ACTIVE, authUser.getUsername());
        }

        authUser.getUser().setState(User.State.APPROVED);

        return new RegisterUserDto(authUser);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InstitutionDto getInstitution(Integer userId) {
        AuthUser authUser = authUserRepository.findById(userId).orElseThrow(() -> new HEException(ErrorMessage.AUTHUSER_NOT_FOUND));
        Member member = (Member) authUser.getUser();

        return new InstitutionDto(member.getInstitution(), true, true);
    }

}
