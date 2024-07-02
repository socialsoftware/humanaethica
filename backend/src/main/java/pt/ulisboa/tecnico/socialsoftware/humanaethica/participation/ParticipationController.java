package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;

import java.security.Principal;
import java.util.List;

@RestController()
public class ParticipationController {
    @Autowired
    ParticipationService participationService;

    @GetMapping("/activities/{activityId}/participations")
    @PreAuthorize("(hasRole('ROLE_MEMBER')) and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<ParticipationDto> getActivityParticipations(@PathVariable Integer activityId) {
        return participationService.getParticipationsByActivity(activityId);
    }

    @GetMapping("/participations/volunteer")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public List<ParticipationDto> getVolunteerParticipations(Principal principal) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return participationService.getVolunteerParticipations(userId);
    }

    @PostMapping("/activities/{activityId}/participations")
    @PreAuthorize("(hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')) or (hasRole('ROLE_VOLUNTEER') and hasPermission(#activityId, 'PARTICIPATION.CREATOR'))")
    public ParticipationDto createParticipation(@PathVariable Integer activityId, @Valid @RequestBody ParticipationDto participationDto, Principal principal) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return participationService.createParticipation(activityId, participationDto, userId);
    }

    @PutMapping("/participations/{participationId}")
    @PreAuthorize("(hasRole('ROLE_MEMBER') and hasPermission(#participationId, 'PARTICIPATION.MANAGER')) or (hasRole('ROLE_VOLUNTEER') and hasPermission(#participationId, 'PARTICIPATION.VOLUNTEER'))")
    public ParticipationDto updateParticipation(@PathVariable Integer participationId,@Valid @RequestBody ParticipationDto participationDto, Principal principal) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return participationService.updateParticipation(participationId, participationDto, userId);
    }

    @DeleteMapping("/participations/{participationId}")
    @PreAuthorize("(hasRole('ROLE_MEMBER')) and hasPermission(#participationId, 'PARTICIPATION.MANAGER')")
    public ParticipationDto deleteParticipation(@PathVariable Integer participationId) {
        return participationService.deleteParticipation(participationId);
    }

}
