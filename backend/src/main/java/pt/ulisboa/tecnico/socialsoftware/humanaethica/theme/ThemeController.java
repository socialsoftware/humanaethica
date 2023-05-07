package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.RegisterThemeDto;
import java.util.List;

@RestController
public class ThemeController {
    @Autowired
    private ThemeService themeService;

    @GetMapping("/themes")
    public List<ThemeDto> getThemes() {
        return themeService.getThemes();
    }

    @DeleteMapping("/themes/{themeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ThemeDto> deleteTheme(@PathVariable int themeId) {
        return themeService.deleteTheme(themeId);
    }

    @PostMapping("/themes/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void registerTheme(@Valid @RequestBody ThemeDto themeDto){
        themeService.registerTheme(themeDto);
    }
}