package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.InstitutionProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService;

import java.security.Principal;
import java.util.Optional;

@RestController()
public class InstitutionProfileController {
    @Autowired
    InstitutionProfileService institutionProfileService;

    @Autowired
    UserService userService;

    @GetMapping("/profile/institution/{institutionId}")
    public Optional<InstitutionProfileDto> getInstitutionProfile(@PathVariable Integer institutionId) {
        return institutionProfileService.getInstitutionProfile(institutionId);
    }

    @PostMapping("/profile/institution")
    @PreAuthorize("(hasRole('ROLE_MEMBER'))")
    public InstitutionProfileDto createInstitutionProfile(Principal principal, @Valid @RequestBody InstitutionProfileDto institutionProfileDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return institutionProfileService.createInstitutionProfile(userService.getInstitution(userId).getId(), institutionProfileDto);
    }
}