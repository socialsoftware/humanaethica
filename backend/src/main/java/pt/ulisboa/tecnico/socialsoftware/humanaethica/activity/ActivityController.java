package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.RegisterActivityDto;

import java.util.List;

@RestController
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @GetMapping("/activities")
    public List<ActivityDto> getActivities() {
        return activityService.getActivities();
    }

    @DeleteMapping("/activity/{activityId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ActivityDto> suspendActivity(@PathVariable Integer activityId) {
        return activityService.suspendActivity(activityId);
    }

    @PostMapping("/activity/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void registerActivity(@Valid @RequestPart("activity") ActivityDto activityDto){
        activityService.registerActivity(activityDto);
    }

    @PostMapping("/activity/{activityId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateActivity(@PathVariable Integer activityId) {
        activityService.validateActivity(activityId);
    }
}
