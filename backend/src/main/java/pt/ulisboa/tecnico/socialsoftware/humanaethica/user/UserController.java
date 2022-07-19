package pt.ulisboa.tecnico.socialsoftware.humanaethica.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserApplicationalService userApplicationalService;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }

    // vale a pena ter este metodo
    @PostMapping("/users/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserDto registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return userApplicationalService.registerUser(registerUserDto);

    }

    @PostMapping("/users/register/confirm")
    public RegisterUserDto confirmRegistration(@RequestBody RegisterUserDto registerUserDto) {
        return userApplicationalService.confirmRegistration(registerUserDto);
    }

    @PostMapping("/users/institutions/{institutionId}/registerMember")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.ACCESS')")
    public UserDto registerMember(@PathVariable int institutionId, @Valid @RequestBody RegisterUserDto registerUserDto) {
        registerUserDto.setRole(Role.MEMBER);
        return userApplicationalService.registerUser(registerUserDto);
    }

    @PostMapping("/users/registerVolunteer")
    public UserDto registerVolunteer(@Valid @RequestBody RegisterUserDto registerUserDto) {
        registerUserDto.setRole(Role.VOLUNTEER);
        return userApplicationalService.registerUser(registerUserDto);
    }

    @PostMapping("/users/{userId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateUser(@PathVariable int userId) {
        userApplicationalService.validateUser(userId);
    }
}
