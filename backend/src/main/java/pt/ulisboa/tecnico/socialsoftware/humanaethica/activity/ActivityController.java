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

    @PutMapping("/activity/{activityId}/suspend")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void suspendActivity(@PathVariable Integer activityId) {
        activityService.suspendActivity(activityId);
    }

    @PostMapping("/activity/register")
    public void registerActivity(@Valid @RequestBody ActivityDto activityDto){
        activityService.registerActivity(activityDto);
    }

    @PutMapping("/activity/{activityId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void validateActivity(@PathVariable int activityId) {
        activityService.validateActivity(activityId);
    }

    @PutMapping("/activity/{activityId}/report")
    public void reportActivity(@PathVariable int activityId) {
        activityService.reportActivity(activityId);
    }


    @PutMapping("/activity/{activityId}/addTheme")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addTheme(@PathVariable int activityId, List<Theme> themes) {
        activityService.addTheme(activityId, themes);
    }

    @PutMapping("/activity/{activityId}/removeTheme")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeTheme(@PathVariable int activityId, List<Theme> themes) {
        activityService.removeTheme(activityId, themes);
    }
}
