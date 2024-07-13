package pt.ulisboa.tecnico.socialsoftware.humanaethica.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain.Report;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class ReportService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ReportRepository reportRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ReportDto> getReportsByActivity(Integer activityId) {
        if (activityId == null) throw  new HEException(ACTIVITY_NOT_FOUND);
        activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        return reportRepository.getReportsByActivityId(activityId).stream()
                .sorted(Comparator.comparing(Report::getReportDateTime))
                .map(ReportDto::new)
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ReportDto> getVolunteerReports(Integer userId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        return reportRepository.getReportsForVolunteerId(userId).stream()
                .sorted(Comparator.comparing(Report::getReportDateTime))
                .map(ReportDto::new)
                .toList();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReportDto createReport(Integer userId, Integer activityId, ReportDto reportDto) {
        if (reportDto == null) throw  new HEException(REPORT_REQUIRES_JUSTIFICATION);

        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        if (activityId == null) throw  new HEException(ACTIVITY_NOT_FOUND);
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        Report report = new Report(activity, volunteer, reportDto);
        reportRepository.save(report);

        return new ReportDto(report);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReportDto removeReport(Integer reportId) {
        if (reportId == null) throw new HEException(REPORT_NOT_FOUND);

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new HEException(REPORT_NOT_FOUND, reportId));
        
        report.delete();

        reportRepository.delete(report);

        return new ReportDto(report);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ReportDto> getVolunteerReportsAsVolunteer(Integer userId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);

        return reportRepository.getReportsForVolunteerId(userId).stream()
                .sorted(Comparator.comparing(Report::getReportDateTime))
                .map(ReportDto::new)
                .toList();
    }



}