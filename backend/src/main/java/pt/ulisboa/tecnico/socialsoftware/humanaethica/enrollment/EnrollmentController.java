package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;

import java.security.Principal;
import java.util.List;

@RestController()
@RequestMapping
public class EnrollmentController {
    @Autowired
    EnrollmentService enrollmentService;

    @GetMapping("/activities/{activityId}/enrollments")
    @PreAuthorize("(hasRole('ROLE_MEMBER')) and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<EnrollmentDto> getActivityEnrollments(@PathVariable Integer activityId) {
        return enrollmentService.getEnrollmentsByActivity(activityId);
    }

    @GetMapping("/enrollments/volunteer")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public List<EnrollmentDto> getVolunteerEnrollments(Principal principal) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return enrollmentService.getVolunteerEnrollments(userId);
    }

    @PostMapping("/activities/{activityId}/enrollments")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public EnrollmentDto createEnrollment(Principal principal, @PathVariable Integer activityId, @Valid @RequestBody EnrollmentDto enrollmentDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return enrollmentService.createEnrollment(userId, activityId, enrollmentDto);
    }

    @PutMapping("/enrollments/{enrollmentId}")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER')) and hasPermission(#enrollmentId, 'ENROLLMENT.MANAGER')")
    public EnrollmentDto updateEnrollemt(@PathVariable Integer enrollmentId, @Valid @RequestBody EnrollmentDto enrollmentDto) {
        return enrollmentService.updateEnrollment(enrollmentId, enrollmentDto);
    }

    @DeleteMapping("/enrollments/{enrollmentId}")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER')) and hasPermission(#enrollmentId, 'ENROLLMENT.MANAGER')")
    public EnrollmentDto removeEnrollment(@PathVariable Integer enrollmentId){
        return enrollmentService.removeEnrollment(enrollmentId);
    }


}
