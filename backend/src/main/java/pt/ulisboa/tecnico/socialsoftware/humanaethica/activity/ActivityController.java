package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @GetMapping()
    public List<ActivityDto> getActivities(Principal principal) {
        return activityService.getActivities();
    }

    @PostMapping()
    @PreAuthorize("(hasRole('ROLE_MEMBER'))")
    public ActivityDto registerActivity(Principal principal, @Valid @RequestBody ActivityDto activityDto){
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activityService.registerActivity(userId, activityDto);
    }

    @PutMapping("/{activityId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public ActivityDto updateActivity(@PathVariable int activityId, @Valid @RequestBody ActivityDto activityDto){
        return activityService.updateActivity(activityId, activityDto);
    }

    @PutMapping("/{activityId}/report")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivityDto reportActivity(@PathVariable int activityId) {
        return activityService.reportActivity(activityId);
    }

    @PutMapping("/{activityId}/suspend/{justification}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER'))")
    public ActivityDto suspendActivity(Principal principal, @PathVariable Integer activityId, @PathVariable String justification) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activityService.suspendActivity(activityId, userId, justification);
    }

    @PutMapping("/{activityId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VOLUNTEER')")
    public ActivityDto validateActivity(@PathVariable int activityId) {
        return activityService.validateActivity(activityId);
    }
}
