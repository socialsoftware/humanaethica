package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;

import java.util.List;

@RestController
public class ThemeController {
    @Autowired
    private ThemeService themeService;

    @GetMapping("/themes")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addInstitution(@PathVariable int themeId, List<Institution> institutions) {
        themeService.addInstitution(themeId, institutions);
    }

    @PutMapping("/theme/{themeId}/removeInstitution")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeInstitution(@PathVariable int themeId, List<Institution> institutions) {
        themeService.removeInstitution(themeId, institutions);
    }
}