package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.UserApplicationalService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.AuthPasswordDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.INVALID_LOGIN_CREDENTIALS;

@RestController
public class AuthController {
    @Autowired
    private AuthUserService authUserService;
    @Autowired
    private DemoService demoService;
    @Autowired
    Environment environment;

    @Autowired
    private UserApplicationalService userApplicationalService;

    @PostMapping("/auth/user")
    public AuthDto loginUserAuth(@Valid @RequestBody AuthPasswordDto authUsernameDto) {
        try {
            return authUserService.loginUserAuth(authUsernameDto.getUsername(), authUsernameDto.getPassword());
        } catch (HEException e) {
            throw new HEException(INVALID_LOGIN_CREDENTIALS);
        }
    }

    @GetMapping("/auth/demo/volunteer")
    public AuthDto loginDemoVolunteerAuth() {
        return this.authUserService.loginDemoVolunteerAuth();
    }

    @GetMapping("/auth/demo/member")
    public AuthDto loginDemoMemberAuth() {
        return this.authUserService.loginDemoMemberAuth();
    }

    @GetMapping("/auth/demo/admin")
    public AuthDto loginDemoAdminAuth() {
        if (environment.acceptsProfiles(Profiles.of("dev"))
                || environment.acceptsProfiles(Profiles.of("test"))
                || environment.acceptsProfiles(Profiles.of("test-int"))) {
            demoService.getDemoAdmin();
            return authUserService.loginUserAuth("ars", "ars");
        }
        return null;
    }


    @PostMapping("/users/register/confirm")
    public RegisterUserDto confirmRegistration(@RequestBody RegisterUserDto registerUserDto) {
        return userApplicationalService.confirmRegistration(registerUserDto);
    }

    @PostMapping("/users/{userId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateUser(@PathVariable int userId) {
        userApplicationalService.validateUser(userId);
    }





}