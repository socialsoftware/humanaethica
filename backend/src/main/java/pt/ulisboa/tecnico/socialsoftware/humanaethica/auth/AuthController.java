package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_LOGIN_CREDENTIALS;

@RestController
public class AuthController {
    @Autowired
    private AuthUserService authUserService;

    @GetMapping("/auth/user")
    public AuthDto loginUserAuth(@RequestParam String username, @RequestParam String password) {
        try {
            return authUserService.loginUserAuth(username, password);
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

}