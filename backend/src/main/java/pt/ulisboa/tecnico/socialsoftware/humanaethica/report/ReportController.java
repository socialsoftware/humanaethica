package pt.ulisboa.tecnico.socialsoftware.humanaethica.report;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.ReportService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto;

import java.security.Principal;
import java.util.List;

@RestController()
@RequestMapping
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/reports/administrator")
    @PreAuthorize("(hasRole('ROLE_ADMIN'))")
    public List<ReportDto> getAdministratorReports(Principal principal) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return reportService.getAdministratorReports(userId);
    }

    @PostMapping("/activities/{activityId}/reports")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public ReportDto createReport(Principal principal, @PathVariable Integer activityId, @Valid @RequestBody ReportDto reportDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return reportService.createReport(userId, activityId, reportDto);
    }




    
}
 