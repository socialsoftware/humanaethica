package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activitySuggestions")
public class ActivitySuggestionController {
    @Autowired
    private ActivitySuggestionService activitySuggestionService;

    @GetMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public List<ActivitySuggestionDto> getActivitySuggestions(@PathVariable Integer institutionId) {
        return this.activitySuggestionService.getActivitySuggestionsByInstitution(institutionId);
    }

    @GetMapping("/volunteer/{volunteerId}")
    // Qual role???
    // Alguma permissão?
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))") //autentificado?
    public List<ActivitySuggestionDto> getActivitySuggestionsByVolunteer(@PathVariable Integer volunteerId) {
        return this.activitySuggestionService.getActivitySuggestionsByVolunteer(volunteerId);
    }

    @PostMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivitySuggestionDto createActivitySuggestion(Principal principal, @PathVariable Integer institutionId, @Valid @RequestBody ActivitySuggestionDto activitySuggestionDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activitySuggestionService.createActivitySuggestion(userId, institutionId, activitySuggestionDto);
    }

    // e se for com o institutionId no path, os métodos também têm de receber o institutionId não?
    @PutMapping("/institution/{institutionId}/approves/{activitySuggestionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')") //TOASK
    public ActivitySuggestionDto approveActivitySuggestion(@PathVariable Integer activitySuggestionId) {
        return activitySuggestionService.approveActivitySuggestion(activitySuggestionId);
    }

    // e se for com o institutionId no path, os métodos também têm de receber o institutionId não?
    @PutMapping("/institution/{institutionId}/rejects/{activitySuggestionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')") //TOASK
    public ActivitySuggestionDto rejectActivitySuggestion(@PathVariable Integer activitySuggestionId) {
        return activitySuggestionService.rejectActivitySuggestion(activitySuggestionId);
    }
}