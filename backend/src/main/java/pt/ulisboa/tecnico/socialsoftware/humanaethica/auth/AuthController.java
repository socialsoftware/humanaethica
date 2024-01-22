package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthPasswordDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_LOGIN_CREDENTIALS;

@RestController
public class AuthController {
    @Autowired
    private AuthUserService authUserService;
    @Autowired
    private DemoService demoService;
    @Autowired
    Environment environment;

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
}