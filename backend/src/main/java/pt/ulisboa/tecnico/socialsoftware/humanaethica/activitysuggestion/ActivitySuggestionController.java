package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

@RestController
@RequestMapping("/activitySuggestions")
public class ActivitySuggestionController {
    @Autowired
    private ActivitySuggestionService activitySuggestionService;

    @GetMapping()
    public List<ActivitySuggestionDto> getAllActivitySuggestions(Principal principal) {
        return this.activitySuggestionService.getAllActivitySuggestions();
    }

    @GetMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public List<ActivitySuggestionDto> getActivitySuggestions(@PathVariable Integer institutionId) {
        return this.activitySuggestionService.getActivitySuggestionsByInstitution(institutionId);
    }

    @GetMapping("/volunteer/{volunteerId}")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public List<ActivitySuggestionDto> getActivitySuggestionsByVolunteer(@PathVariable Integer volunteerId) {
        return this.activitySuggestionService.getActivitySuggestionsByVolunteer(volunteerId);
    }

    @PostMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivitySuggestionDto createActivitySuggestion(Principal principal, @PathVariable Integer institutionId, @Valid @RequestBody ActivitySuggestionDto activitySuggestionDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activitySuggestionService.createActivitySuggestion(userId, institutionId, activitySuggestionDto);
    }

    @PutMapping("/{activitySuggestionId}/institution/{institutionId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivitySuggestionDto updateActivitySuggestion(@PathVariable int activitySuggestionId, @Valid @RequestBody ActivitySuggestionDto activitySuggestionDto, @PathVariable Integer institutionId){
        return activitySuggestionService.updateActivitySuggestion(activitySuggestionId, activitySuggestionDto, institutionId);
    }

    @PutMapping("/institution/{institutionId}/approves/{activitySuggestionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public ActivitySuggestionDto approveActivitySuggestion(@PathVariable Integer institutionId, @PathVariable Integer activitySuggestionId) {
        return activitySuggestionService.approveActivitySuggestion(activitySuggestionId);
    }

    @PutMapping("/institution/{institutionId}/rejects/{activitySuggestionId}/{justification}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public ActivitySuggestionDto rejectActivitySuggestion(@PathVariable Integer institutionId, @PathVariable Integer activitySuggestionId, @PathVariable String justification) {
        return activitySuggestionService.rejectActivitySuggestion(activitySuggestionId, justification);
    }

    @PutMapping("/volunteer/community/upvotes/{activitySuggestionId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivitySuggestionDto upvoteActivitySuggestion(Principal principal, @PathVariable Integer activitySuggestionId) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activitySuggestionService.upvoteActivitySuggestion(userId, activitySuggestionId);
    }

    @PutMapping("/volunteer/community/upvotes/{activitySuggestionId}/remove")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivitySuggestionDto removeUpvoteActivitySuggestion(Principal principal, @PathVariable Integer activitySuggestionId) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activitySuggestionService.removeUpvoteActivitySuggestion(userId, activitySuggestionId);
    }

    @GetMapping("/volunteer/community/votedSuggestions")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public List<ActivitySuggestionDto> getVotedActivitySuggestions(Principal principal) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activitySuggestionService.getVotedActivitySuggestions(userId);
    }
}