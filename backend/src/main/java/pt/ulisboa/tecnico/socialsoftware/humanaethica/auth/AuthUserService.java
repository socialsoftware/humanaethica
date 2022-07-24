package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoUtils;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.sql.SQLException;
import java.util.Optional;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_PASSWORD;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class AuthUserService {
    @Autowired
    private UserService userService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto loginUserAuth(String username, String password) {
        Optional<AuthUser> optionalAuthUser = authUserRepository.findAuthUserByUsername(username);
        if (optionalAuthUser.isEmpty()) {
            throw new HEException(USER_NOT_FOUND, username);
        }

        AuthUser authUser = optionalAuthUser.get();

        if (password == null ||
                !passwordEncoder.matches(password, authUser.getPassword())) {
            throw new HEException(INVALID_PASSWORD, password);
        }
        authUser.setLastAccess(DateHandler.now());

        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto loginDemoVolunteerAuth() {
        AuthUser authUser = getDemoVolunteer();

        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthDto loginDemoMemberAuth() {
        AuthUser authUser = getDemoMember();

        return new AuthDto(JwtTokenProvider.generateToken(authUser), new AuthUserDto(authUser));
    }

    private AuthUser getDemoMember() {
        return authUserRepository.findAuthUserByUsername(DemoUtils.DEMO_MEMBER).orElseGet(() -> {
            AuthUser authUser = userService.createMemberWithAuth(DemoUtils.DEMO_MEMBER, DemoUtils.DEMO_MEMBER, "demo_member@mail.com", AuthUser.Type.DEMO, institutionService.getDemoInstitution(), State.ACTIVE);
            return authUser;
        });
    }

    private AuthUser getDemoVolunteer() {
        return authUserRepository.findAuthUserByUsername(DemoUtils.DEMO_VOLUNTEER).orElseGet(() -> {
            AuthUser authUser = userService.createVolunteerWithAuth(DemoUtils.DEMO_VOLUNTEER, DemoUtils.DEMO_VOLUNTEER, "demo_volunteer@mail.com", AuthUser.Type.DEMO, State.ACTIVE);
            return authUser;
        });
    }

}