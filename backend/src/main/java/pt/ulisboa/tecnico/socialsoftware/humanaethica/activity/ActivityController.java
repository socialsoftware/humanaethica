package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;

import java.util.List;

@RestController
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

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
    public void registerActivity(@Valid @RequestBody ActivityDto activityDto){
        activityService.registerActivity(activityDto);
    }

    @PostMapping("/activity/{activityId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateActivity(@PathVariable Integer activityId) {
        activityService.validateActivity(activityId);
    }

    @PostMapping("/activity/{activityId}/addTheme")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addTheme(@PathVariable Integer activityId, List<Theme> themes) {
        activityService.addTheme(activityId, themes);
    }

}
