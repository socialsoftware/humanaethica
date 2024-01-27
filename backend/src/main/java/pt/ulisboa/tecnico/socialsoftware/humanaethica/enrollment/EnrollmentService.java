package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;

import java.util.Comparator;
import java.util.List;

public class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<EnrollmentDto> getEnrollmentsByActivity(Integer activityId) {
        return enrollmentRepository.getEnrollmentsByActivityId(activityId).stream()
                .sorted(Comparator.comparing(Enrollment::getEnrollmentDateTime))
                .map(EnrollmentDto::new)
                .toList();
    }
}
