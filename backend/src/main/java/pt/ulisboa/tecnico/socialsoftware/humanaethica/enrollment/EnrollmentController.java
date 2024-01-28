package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;

import java.security.Principal;
import java.util.List;

@RestController()
@RequestMapping(value = "/activities/{activityId}/enrollments")
public class EnrollmentController {
    @Autowired
    EnrollmentService enrollmentService;

    @GetMapping()
    public List<EnrollmentDto> getActivities(@PathVariable Integer activityId) {
        return enrollmentService.getEnrollmentsByActivity(activityId);
    }

    @PostMapping()
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public EnrollmentDto createEnrollment(Principal principal, @PathVariable Integer activityId, @Valid @RequestBody EnrollmentDto enrollmentDto){
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return enrollmentService.createEnrollment(userId, activityId, enrollmentDto);
    }
}
