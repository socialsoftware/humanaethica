package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthPasswordDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_LOGIN_CREDENTIALS;

@RestController
public class AuthController {
    @Autowired
    private AuthUserService authUserService;

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

}