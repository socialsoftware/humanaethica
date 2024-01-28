package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;

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
}
