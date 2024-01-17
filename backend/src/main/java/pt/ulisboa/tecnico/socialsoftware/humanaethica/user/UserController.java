package pt.ulisboa.tecnico.socialsoftware.humanaethica.user;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.UserDocument;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
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
        return userService.deleteUser(userId);
    }

    @GetMapping("/user/{userId}/document")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<byte[]> getInstitutionDocument(@PathVariable int userId) {
        UserDocument doc = userService.getDocument(userId);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_PDF;
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\"" + doc.getName() + "\"");
        return ResponseEntity.ok().contentType(type).headers(headers).body(doc.getContent());
    }

    @PostMapping("/users/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserDto registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) throws IOException {
        return userService.registerUser(registerUserDto, null);

    }

    @PostMapping("/users/register/confirm")
    public RegisterUserDto confirmRegistration(@RequestBody RegisterUserDto registerUserDto) {
        return userApplicationalService.confirmRegistration(registerUserDto);
    }

    @PostMapping("/users/registerInstitutionMember")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public void registerMember(Principal principal,
                               @Valid @RequestPart(value = "member") RegisterUserDto registerUserDto,
                               @RequestParam(value = "file") MultipartFile document) throws IOException {
        Member member = (Member) ((AuthUser) ((Authentication) principal).getPrincipal()).getUser();

        registerUserDto.setRole(Role.MEMBER);
        registerUserDto.setInstitutionId(member.getInstitution().getId());

        userService.registerUser(registerUserDto, document);
    }
    @PostMapping("/users/registerVolunteer")
    public void registerVolunteer(@Valid @RequestPart("volunteer") RegisterUserDto registerUserDto, @RequestParam(value = "file") MultipartFile document) throws IOException {
        registerUserDto.setRole(Role.VOLUNTEER);

        userService.registerUser(registerUserDto, document);
    }

    @PostMapping("/users/{userId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateUser(@PathVariable int userId) {
        userApplicationalService.validateUser(userId);
    }

    @GetMapping("/users/{userId}/getInstitution")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public InstitutionDto getInstitution(@PathVariable int userId) {
        return userService.getInstitution(userId);
    }

}
