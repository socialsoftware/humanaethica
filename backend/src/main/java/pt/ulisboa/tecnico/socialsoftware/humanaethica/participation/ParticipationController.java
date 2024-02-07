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
@RequestMapping(value = "/activities/{activityId}/participations")
public class ParticipationController {
    @Autowired
    ParticipationService participationService;

    @GetMapping()
    @PreAuthorize("(hasRole('ROLE_MEMBER')) and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<ParticipationDto> getActivityParticipations(@PathVariable Integer activityId) {
        return participationService.getParticipationsByActivity(activityId);
    }

    @PostMapping()
    @PreAuthorize("(hasRole('ROLE_MEMBER')) and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public ParticipationDto createParticipation(Principal principal, @PathVariable Integer activityId, @Valid @RequestBody ParticipationDto participationDto) {
        return participationService.createParticipation(activityId, participationDto);
    }
}
