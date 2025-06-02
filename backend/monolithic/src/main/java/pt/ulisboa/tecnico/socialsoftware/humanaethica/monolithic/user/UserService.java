package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserDeletedEvent;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserRegisteredEvent;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.repository.UserDocumentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.repository.UserRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Autowired
    private EventBus eventBus;



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
    public List<UserDto> deleteUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));

        Institution i = user.remove();
        if (i != null)
            institutionRepository.save(i);


        eventBus.post(new UserDeletedEvent(userId));
        return getUsers();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDocument getDocument(Integer id) {
        return (userRepository.findById(id).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND))).getDocument();
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InstitutionDto getInstitution(Integer userId) {
        Member member = (Member) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));

        return new InstitutionDto(member.getInstitution(), true, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public UserDto registerUser(RegisterUserDto registerUserDto, MultipartFile document) throws IOException {

        if (registerUserDto.getRole() == null) {
            throw new HEException(INVALID_ROLE, "null");
        }
        if (userRepository.findUserByUsername(registerUserDto.getUsername()).isPresent()) {
            throw new HEException(USERNAME_ALREADY_EXIST, (registerUserDto.getUsername()));
        }
        if (registerUserDto.getUsername() == null || registerUserDto.getUsername().trim().length() == 0) {
            throw new HEException(INVALID_AUTH_USERNAME, registerUserDto.getUsername());
        }
        if (registerUserDto.getEmail() == null || !registerUserDto.getEmail().matches(UserService.MAIL_FORMAT))
            throw new HEException(INVALID_EMAIL, registerUserDto.getEmail());

        User user = switch (registerUserDto.getRole()) {
            case VOLUNTEER -> new Volunteer(registerUserDto.getName(),
                    registerUserDto.getUsername(), registerUserDto.getEmail(), State.SUBMITTED);
            case MEMBER -> {
                Institution institution = institutionRepository.findById(registerUserDto.getInstitutionId()).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, registerUserDto.getInstitutionId()));
                yield new Member(registerUserDto.getName(),
                        registerUserDto.getUsername(), registerUserDto.getEmail(), institution, State.SUBMITTED);
            }
            case ADMIN -> new Admin(registerUserDto.getName(),
                    registerUserDto.getUsername(), registerUserDto.getEmail(), State.SUBMITTED);
            default -> throw new HEException(INVALID_ROLE, registerUserDto.getRole().name());
        };

        user = userRepository.save(user);

        if (document != null) {
            UserDocument userDocument = new UserDocument();
            userDocument.setName(document.getName());
            userDocument.setContent(document.getBytes());

            user.setDocument(userDocument);
            userDocumentRepository.save(userDocument);
        }



        UserRegisteredEvent event = new UserRegisteredEvent(registerUserDto, user.getId(), Type.NORMAL, user.getRole(), user.getName());
        eventBus.post(event);


        return new UserDto(user);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer createVolunteer(String name, String username, String email,  State state){
        Volunteer volunteer = new Volunteer(name, username, email, state);
        return userRepository.save(volunteer).getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer createMember(String name, String username, String email, Institution institution, State state) {
        Member member = new Member(name, username, email, institution, state);
        return userRepository.save(member).getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer createAdmin(String name, String username, String email, State state) {
        Admin admin = new Admin(name, username, email, state);
        return userRepository.save(admin).getId();
    }


    @Transactional
    public void changeState(Integer userId, User.State newState) {
        User user = userRepository.findById(userId).orElseThrow(() -> new HEException(ErrorMessage.USER_NOT_FOUND));
        user.setState(newState);
        userRepository.save(user);
    }

    public User.State getUserState(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new HEException(ErrorMessage.USER_NOT_FOUND))
                .getState();
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new HEException(ErrorMessage.USER_NOT_FOUND));
    }

}
