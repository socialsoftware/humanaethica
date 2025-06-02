package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.security.Principal;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security.UserInfo;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Member;

import java.util.List;

@RestController
public class ThemeController {
    @Autowired
    private ThemeService themeService;

    @Autowired
    private UserService userService;

    @GetMapping("/themes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ThemeDto> getThemes() {
        return themeService.getThemes();
    }

    @DeleteMapping("/themes/{themeId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ThemeDto> deleteTheme(@PathVariable int themeId) {
        return themeService.deleteTheme(themeId);
    }

    @PutMapping("/themes/{themeId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ThemeDto> validateTheme(@PathVariable int themeId) {
        return themeService.validateTheme(themeId);
    }

    @PostMapping("/themes/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void registerTheme(@Valid @RequestBody ThemeDto themeDto){
        themeService.registerTheme(themeDto,true);
    }

    @PostMapping("/themes/registerInstitution")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public void registerThemeInstitution(@Valid @RequestBody ThemeDto themeDto){
        themeService.registerTheme(themeDto,false);
    }

    @PutMapping("/theme/{themeId}/addInstitution")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public void addInstitution(@PathVariable int themeId, Principal principal) {
        UserInfo userinfo = ((UserInfo) ((Authentication) principal).getPrincipal());
        Member member = (Member) userService.getUserById(userinfo.getId());
        themeService.addInstitutionToTeam(themeId, member.getInstitution().getId());
    }

    @PutMapping("/theme/{themeId}/removeInstitution")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public void removeInstitution(@PathVariable int themeId, Principal principal) {
        UserInfo userinfo = ((UserInfo) ((Authentication) principal).getPrincipal());
        Member member = (Member) userService.getUserById(userinfo.getId());
        themeService.removeInstitutionFromTeam(themeId, member.getInstitution().getId());
    }

    @GetMapping("/theme/getInstitutionThemes")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public List<ThemeDto> getInstitutionThemes(Principal principal){
        UserInfo userinfo = ((UserInfo) ((Authentication) principal).getPrincipal());
        Member member = (Member) userService.getUserById(userinfo.getId());
        return themeService.getInstitutionThemes(member.getInstitution().getId());
    }

    @GetMapping("/theme/availableThemesforInstitution")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public List<ThemeDto> availableThemesForInstitution(Principal principal){
        UserInfo userinfo = ((UserInfo) ((Authentication) principal).getPrincipal());
        Member member = (Member) userService.getUserById(userinfo.getId());;
        return themeService.availableThemesForInstitution(member.getInstitution().getId());
    }

    @GetMapping("/themes/availableThemes")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    public List<ThemeDto> availableThemes(){
        return themeService.getAvailableThemes();
    }
}