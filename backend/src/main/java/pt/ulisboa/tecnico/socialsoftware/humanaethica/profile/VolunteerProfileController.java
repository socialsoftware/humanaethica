package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.VolunteerProfileDto;

@RestController()
public class VolunteerProfileController {
    @Autowired
    VolunteerProfileService volunteerProfileService;

    @GetMapping("/profile/volunteer/{volunteerId}")
    public Optional<VolunteerProfileDto> getVolunteerProfile(@PathVariable Integer volunteerId) {
        return volunteerProfileService.getVolunteerProfile(volunteerId);
    }

    @PostMapping("/profile/volunteer")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public VolunteerProfileDto createVolunteerProfile(Principal principal, @Valid @RequestBody VolunteerProfileDto volunteerProfileDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return volunteerProfileService.createVolunteerProfile(userId, volunteerProfileDto);
    }

    @GetMapping("/profile/volunteer/views")
    public List<VolunteerProfileDto> getListVolunteerProfile() {
        return volunteerProfileService.getListVolunteerProfile();
    }
}