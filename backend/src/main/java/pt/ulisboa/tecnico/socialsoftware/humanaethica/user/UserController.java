package pt.ulisboa.tecnico.socialsoftware.humanaethica.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.UserDocument;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;

import java.io.IOException;
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
    public List<UserDto> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return userService.getUsers();
    }

    @GetMapping("/user/{userId}/document")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<byte[]> getInstitutionDocument(@PathVariable int userId) {
        UserDocument doc = userApplicationalService.getUserDocument(userId);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_PDF;
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\"" + doc.getName() + "\"");
        return ResponseEntity.ok().contentType(type).headers(headers).body(doc.getContent());
    }

    // vale a pena ter este metodo
    @PostMapping("/users/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserDto registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return userService.registerUser(registerUserDto);

    }

    @PostMapping("/users/register/confirm")
    public RegisterUserDto confirmRegistration(@RequestBody RegisterUserDto registerUserDto) {
        return userApplicationalService.confirmRegistration(registerUserDto);
    }

    @PostMapping("/users/{memberId}/registerMember")
    public void registerMember(@PathVariable int memberId, @Valid @RequestPart(value = "member") RegisterUserDto registerUserDto, @RequestParam(value = "file") MultipartFile doc) throws IOException {
        registerUserDto.setRole(Role.MEMBER);

        UserDocument document = new UserDocument();
        document.setName(doc.getName());
        document.setContent(doc.getBytes());
        userApplicationalService.setInstitutionFromMember(registerUserDto, memberId);

        UserDto userDto = userService.registerUser(registerUserDto);
        userService.addDocument(userDto, document);
    }

    @PostMapping("/users/registerVolunteer")
    public void registerVolunteer(@Valid @RequestPart("volunteer") RegisterUserDto registerUserDto, @RequestParam(value = "file") MultipartFile doc) throws IOException {
        registerUserDto.setRole(Role.VOLUNTEER);

        UserDocument document = new UserDocument();
        document.setName(doc.getName());
        document.setContent(doc.getBytes());

        UserDto userDto = userService.registerUser(registerUserDto);
        userService.addDocument(userDto, document);

    }

    @PostMapping("/users/{userId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateUser(@PathVariable int userId) {
        userApplicationalService.validateUser(userId);
    }
}
