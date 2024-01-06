package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
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
        List<Theme> allThemes = themeRepository.findAll();

        return allThemes.stream()
                .sorted(Comparator.comparing(Theme::getAbsoluteName, String.CASE_INSENSITIVE_ORDER))
                .map(theme -> new ThemeDto(theme, true, true, false))
                .toList();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Theme registerTheme(ThemeDto themeDto, boolean isAdmin) {
        if (themeDto.getName() == null) {
            throw new HEException(INVALID_THEME_NAME, themeDto.getName());
        }

       if (themeRepository.countByName(themeDto.getName()) > 0) {
           throw new HEException(THEME_ALREADY_EXISTS);
        }
        Theme themeParent = null;
        if (themeDto.getParentTheme() != null) {
            themeParent = themeRepository.findById(themeDto.getParentTheme().getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeDto.getParentTheme().getId())));
        }
        Theme theme;

        if (isAdmin || themeParent != null) {
            theme = new Theme(themeDto.getName(), Theme.State.APPROVED, themeParent);
        }
        else {
            theme = new Theme(themeDto.getName(), Theme.State.SUBMITTED, themeParent);
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
    public void addInstitution(Integer themeId, int institutionId) {
        if (themeId == null) {
            throw new HEException(THEME_NOT_FOUND);
        }
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        institution.addTheme(theme);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeInstitution(Integer themeId, int institutionId) {
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
        InstitutionDto institutionDto = new InstitutionDto(institution,true, true);
        return institutionDto.getThemes();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> availableThemesForInstitution(Integer institutionId) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        Set<Integer> institutionThemeIds = institution.getThemes().stream()
                .map(Theme::getId)
                .collect(Collectors.toSet());

        return getThemes().stream()
                    .filter(themeDto -> themeDto.getState().equals("APPROVED"))
                    .filter(themeDto -> !institutionThemeIds.contains(themeDto.getId()))
                    .toList();

//        List<ThemeDto> aux = getThemes();
//        List<ThemeDto> filteredThemes = new ArrayList<>();
//
//        for (ThemeDto x : aux) {
//            if (!(x.getState().equals("APPROVED"))) {
//                continue; // Ignora temas não aprovados
//            }
//            boolean isInInstitution = false;
//            for (Theme y : institution.getThemes()) {
//                if (x.getId().equals(y.getId())) {
//                    isInInstitution = true;
//                    break;
//                }
//            }
//            if (!isInInstitution) {
//                filteredThemes.add(x);
//            }
//        }
//
//        return filteredThemes;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)

    public List<ThemeDto> getAvailableThemes() {
        return getThemes().stream()
                .filter(themeDto -> themeDto.getState().equals("APPROVED"))
                .toList();
    }

}
