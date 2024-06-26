package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class AssessmentService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private AssessmentRepository assessmentRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> getAssessmentsByInstitution(Integer institutionId) {
        if (institutionId == null) throw  new HEException(INSTITUTION_NOT_FOUND);
        institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, institutionId));

        return assessmentRepository.getAssessmentsByInstitutionId(institutionId).stream()
                .sorted(Comparator.comparing(Assessment::getReviewDate))
                .map(AssessmentDto::new)
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> getAssessmentsByVolunteer(Integer userId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
       userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        return assessmentRepository.getAssessmentsByVolunteerId(userId).stream()
                .sorted(Comparator.comparing(Assessment::getReviewDate))
                .map(AssessmentDto::new)
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto createAssessment(Integer userId, Integer institutionId, AssessmentDto assessmentDto) {
        if (assessmentDto == null) throw  new HEException(ASSESSMENT_REQUIRES_REVIEW);

        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        if (institutionId == null) throw  new HEException(INSTITUTION_NOT_FOUND);
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, institutionId));

        Assessment assessment = new Assessment(institution, volunteer, assessmentDto);
        assessmentRepository.save(assessment);

        return new AssessmentDto(assessment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto deleteAssessment(Integer assessmentId) {
        if (assessmentId == null) throw new HEException(ASSESSMENT_NOT_FOUND);
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new HEException(ASSESSMENT_NOT_FOUND));

        assessmentRepository.delete(assessment);

        return new AssessmentDto(assessment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto updateAssessment(Integer assessmentId, AssessmentDto assessmentDto) {
        if (assessmentId == null) throw new HEException(ASSESSMENT_NOT_FOUND);
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(() -> new HEException(ASSESSMENT_NOT_FOUND, assessmentId));

        assessment.update(assessmentDto);

        return new AssessmentDto(assessment);
    }
}
