package pt.ulisboa.tecnico.socialsoftware.humanaethica.report;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain.Report;

import java.util.List;

@Repository
@Transactional
public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query("SELECT r FROM Report r WHERE r.activity.id = :activityId")
    List<Report> getReportsByActivityId(Integer activityId);

    @Query("SELECT r FROM Report r WHERE r.volunteer.id = :userId")
    List<Report> getReportsForVolunteerId(Integer userId);
}
