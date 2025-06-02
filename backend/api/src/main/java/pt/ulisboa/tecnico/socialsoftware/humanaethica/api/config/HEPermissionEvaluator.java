package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security.UserInfo;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.participation.ParticipationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.EnrollmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.report.domain.Report;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.report.ReportRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.repository.UserRepository;


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

    @Autowired
    private UserRepository userRepository;


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        UserInfo userInfo = ((UserInfo) authentication.getPrincipal());
        int userId = userInfo.getId();

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "ACTIVITY.MEMBER":
                    Activity activity = activityRepository.findById(id).orElse(null);
                    if (activity == null) return false;
                    return activity.getInstitution().getId().equals(getInstitutionId(userId));
                case "PARTICIPATION.MANAGER":
                    Participation participation = participationRepository.findById(id).orElse(null);
                    if (participation == null) return false;
                    return participation.getActivity().getInstitution().getId().equals(getInstitutionId(userId));
                case "ENROLLMENT.MANAGER":
                    Enrollment enrollment = enrollmentRepository.findById(id).orElse(null);
                    if (enrollment == null) return false;
                    return enrollment.getVolunteer().getId().equals(userId);
                case "REPORT.MANAGER":
                    Report report = reportRepository.findById(id).orElse(null);
                    if (report == null) return false;
                    return report.getVolunteer().getId().equals(userId);
                case "ASSESSMENT.WRITER":
                    Assessment assessment = assessmentRepository.findById(id).orElse(null);
                    if (assessment == null) return false;
                    return assessment.getVolunteer().getId().equals(userId);
                case "PARTICIPATION.VOLUNTEER":
                    Participation participation1 = participationRepository.findById(id).orElse(null);
                    if (participation1 == null) return false;
                    return participation1.getVolunteer().getId().equals(userId);
                default:
                    return false;
            }
        }

        return false;
    }

    private Integer getInstitutionId(Integer userId){
        Member member = (Member) userRepository.findById(userId).orElse(null);
        if (member == null) return -1;
        return member.getInstitution().getId();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

}
