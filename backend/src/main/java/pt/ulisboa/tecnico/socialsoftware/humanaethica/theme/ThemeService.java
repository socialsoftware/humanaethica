package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ThemeService {

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstitutionRepository institutionRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> getThemes() {
        return getThemeDtos();
    }

    private List<ThemeDto> getThemeDtos() {
        List<Theme> allThemes = themeRepository.findAll();

        return allThemes.stream()
                .sorted(Comparator.comparing(Theme::getCompleteName, String.CASE_INSENSITIVE_ORDER))
                .map(theme -> new ThemeDto(theme, true, true, false))
                .toList();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Theme registerTheme(ThemeDto themeDto, boolean isAdmin) {
        if (themeDto.getName() == null || themeDto.getName().trim().isEmpty()) {
            throw new HEException(INVALID_THEME_NAME, themeDto.getName());
        }

        if (themeRepository.getThemesByName(themeDto.getName()).stream()
                .anyMatch(theme -> (theme.getParentTheme() == null && themeDto.getParentTheme() == null)
                        || (theme.getParentTheme() != null
                                && themeDto.getParentTheme() != null
                                && theme.getParentTheme().getId().equals(themeDto.getParentTheme().getId())))) {
            throw new HEException(THEME_ALREADY_EXISTS);
        }

        Theme parentTheme = null;
        if (themeDto.getParentTheme() != null) {
            parentTheme = themeRepository.findById(themeDto.getParentTheme().getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeDto.getParentTheme().getId())));
        }

        Theme theme;
        if (isAdmin) {
            theme = new Theme(themeDto.getName(), Theme.State.APPROVED, parentTheme);
        }
        else {
            theme = new Theme(themeDto.getName(), Theme.State.SUBMITTED, parentTheme);
        }

        themeRepository.save(theme);
        return theme;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> deleteTheme(int themeId) {
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        theme.delete();
        return getThemes();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> validateTheme(int themeId) {
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        theme.approve();
        return getThemes();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addInstitutionToTeam(Integer themeId, int institutionId) {
        if (themeId == null) {
            throw new HEException(THEME_NOT_FOUND);
        }
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        institution.addTheme(theme);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeInstitutionFromTeam(Integer themeId, int institutionId) {
        if (themeId == null) {
            throw new HEException(THEME_NOT_FOUND);
        }
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        institution.removeTheme(theme);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> getInstitutionThemes(int institutionId){
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));
        return institution.getThemes().stream()
                .map(theme -> new ThemeDto(theme, false, true, false))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> availableThemesForInstitution(Integer institutionId) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        Set<Integer> institutionThemeIds = institution.getThemes().stream()
                .map(Theme::getId)
                .collect(Collectors.toSet());

        return getThemeDtos().stream()
                    .filter(themeDto -> themeDto.getState().equals(Theme.State.APPROVED.name()))
                    .filter(themeDto -> !institutionThemeIds.contains(themeDto.getId()))
                    .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> getAvailableThemes() {
        return getThemeDtos().stream()
                .filter(themeDto -> themeDto.getState().equals(Theme.State.APPROVED.name()))
                .toList();
    }

}
