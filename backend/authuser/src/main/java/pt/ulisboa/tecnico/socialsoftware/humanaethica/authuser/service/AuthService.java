package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthNormalUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.*;

@Service
public class AuthService {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createVolunteerWithAuth(String name, String username, String email, Type type, User.State state) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new HEException(DUPLICATE_USER, username);
        }

        Integer volunteerId = userService.createVolunteer(name, username, email, state);
        return authUserRepository.save(AuthUser.createAuthUser(volunteerId, username, email, type, Role.VOLUNTEER,name));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUser createMemberWithAuth(String name, String username, String email, Type type, Institution institution, User.State state) {
        if (authUserRepository.findAuthUserByUsername(username).isPresent()) {
            throw new HEException(DUPLICATE_USER, username);
        }

        Integer memberId = userService.createMember(name, username, email, institution, state);

        return authUserRepository.save(AuthUser.createAuthUser(memberId, username, email, type, Role.MEMBER, name));
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

        userService.changeState(authUser.getUserID(), User.State.ACTIVE);
        User user = userService.getUserById(authUser.getUserID());

        return convertToRegisterUserDto(authUser, user);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RegisterUserDto validateUser(Integer userId) {
        AuthNormalUser authUser = (AuthNormalUser) authUserRepository.findById(userId).orElseThrow(() -> new HEException(ErrorMessage.AUTHUSER_NOT_FOUND));
        if (authUser.isActive() || userService.getUserState(authUser.getUserID()).equals(User.State.ACTIVE)){
            throw new HEException(ErrorMessage.USER_ALREADY_ACTIVE, authUser.getUsername());
        }

        userService.changeState(authUser.getUserID(), User.State.APPROVED);
        return convertToRegisterUserDto(authUser, userService.getUserById(authUser.getUserID()));
    }



    public RegisterUserDto convertToRegisterUserDto(AuthNormalUser authUser, User user) {
        RegisterUserDto dto = new RegisterUserDto();

        dto.setId(authUser.getId());
        dto.setName(authUser.getName());
        dto.setUsername(authUser.getUsername());
        dto.setEmail(authUser.getEmail());
        dto.setPassword(authUser.getPassword());
        dto.setRole(authUser.getRole());
        dto.setActive(authUser.isActive());
        dto.setConfirmationToken(authUser.getConfirmationToken());
        if (authUser.getRole() == Role.MEMBER) {
            dto.setInstitutionId(((Member) user).getInstitution().getId());
        }
        if (user instanceof Member member) {
            dto.setInstitutionActive( member.getInstitution().isActive());
        }

        return dto;
    }

}
