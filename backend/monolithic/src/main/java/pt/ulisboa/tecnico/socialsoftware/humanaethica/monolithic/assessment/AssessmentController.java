package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security.UserInfo;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.dto.AssessmentDto;

import java.security.Principal;
import java.util.List;

@RestController()
public class AssessmentController {
    @Autowired
    AssessmentService assessmentService;

    @GetMapping("/institutions/{institutionId}/assessments")
    public List<AssessmentDto> getInstitutionAssessments(@PathVariable Integer institutionId) {
        return assessmentService.getAssessmentsByInstitution(institutionId);
    }

    @GetMapping("/assessments/volunteer")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public List<AssessmentDto> getVolunteerAssessments(Principal principal) {
        int userId = ((UserInfo) ((Authentication) principal).getPrincipal()).getId();
        return assessmentService.getAssessmentsByVolunteer(userId);
    }

    @PostMapping("/institutions/{institutionId}/assessments")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public AssessmentDto createAssessment(Principal principal, @PathVariable Integer institutionId, @Valid @RequestBody AssessmentDto assessmentDto) {
        int userId = ((UserInfo) ((Authentication) principal).getPrincipal()).getId();
        return assessmentService.createAssessment(userId, institutionId, assessmentDto);
    }

    @DeleteMapping("/assessments/{assessmentId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER') and hasPermission(#assessmentId, 'ASSESSMENT.WRITER')")
    public AssessmentDto deleteAssessment(Principal principal, @PathVariable int assessmentId) {
        return assessmentService.deleteAssessment(assessmentId);
    }

    @PutMapping("/assessments/{assessmentId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER') and hasPermission(#assessmentId, 'ASSESSMENT.WRITER')")
    public AssessmentDto updateAssessment(@PathVariable int assessmentId, @Valid @RequestBody AssessmentDto assessmentDto){
        return assessmentService.updateAssessment(assessmentId, assessmentDto);
    }
}
