package pt.ulisboa.tecnico.socialsoftware.humanaethica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.EnrollmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain.Report;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.ReportRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;


import java.io.Serializable;

@Component
public class HEPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private ReportRepository reportRepository;


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        AuthUser authUser = ((AuthUser) authentication.getPrincipal());

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "ACTIVITY.MEMBER":
                    Activity activity = activityRepository.findById(id).orElse(null);
                    if (activity == null) return false;
                    return activity.getInstitution().getId().equals(((Member)authUser.getUser()).getInstitution().getId());
                case "PARTICIPATION.MANAGER":
                    Participation participation = participationRepository.findById(id).orElse(null);
                    if (participation == null) return false;
                    return participation.getActivity().getInstitution().getId().equals(((Member)authUser.getUser()).getInstitution().getId());
                case "ENROLLMENT.MANAGER":
                    Enrollment enrollment = enrollmentRepository.findById(id).orElse(null);
                    if (enrollment == null) return false;
                    return enrollment.getVolunteer().getId().equals(((Volunteer)authUser.getUser()).getId());
                case "REPORT.MANAGER":
                    Report report = reportRepository.findById(id).orElse(null);
                    if (report == null) return false;
                    return report.getVolunteer().getId().equals(((Volunteer)authUser.getUser()).getId());
                case "ASSESSMENT.WRITER":
                    Assessment assessment = assessmentRepository.findById(id).orElse(null);
                    if (assessment == null) return false;
                    return assessment.getVolunteer().getId().equals(((Volunteer)authUser.getUser()).getId());
                case "PARTICIPATION.VOLUNTEER":
                    Participation participation1 = participationRepository.findById(id).orElse(null);
                    if (participation1 == null) return false;
                    return participation1.getVolunteer().getId().equals(((Volunteer)authUser.getUser()).getId());
                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

}
