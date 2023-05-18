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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        return themeRepository.findAll().stream()
                .map(theme->new ThemeDto(theme,false))
                .sorted(Comparator.comparing(ThemeDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Theme registerTheme(ThemeDto themeDto, boolean isAdmin) {

        if (themeDto.getName() == null) {
            throw new HEException(INVALID_THEME_NAME, themeDto.getName());
        }
        for (Theme the : themeRepository.findAll()) {
            if (the.getName().equals(themeDto.getName())) {
                throw new HEException(THEME_ALREADY_EXISTS);
            }
        }
        Theme theme;

        if (isAdmin){
            theme = new Theme(themeDto.getName(), Theme.State.APPROVED);
        }
        else{
            theme = new Theme(themeDto.getName(), Theme.State.SUBMITTED);
        }

        themeRepository.save(theme);
        return theme;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> deleteTheme(int themeId) {
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        if (theme.getInstitutions().size() == 0) {
            theme.delete();
            return getThemes();
        }
        else{
            throw new HEException(THEME_HAS_INSTITUTIONS, theme.getName());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> validateTheme(int themeId) {
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        theme.setState(Theme.State.APPROVED);
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

    public List<ThemeDto> getInstitutionThemes(int institutionId){
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));
        InstitutionDto institutionDto = new InstitutionDto(institution,false);
        return institutionDto.getThemes();
    }

    public List<ThemeDto> availableThemesforInstitution(Integer institutionId) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        List<ThemeDto> aux = getThemes();
        List<ThemeDto> filteredThemes = new ArrayList<>();

        for (ThemeDto x : aux) {
            if (!(x.getState().equals("APPROVED"))) {
                continue; // Ignora temas não aprovados
            }
            boolean isInInstitution = false;
            for (Theme y : institution.getThemes()) {
                if (x.getId().equals(y.getId())) {
                    isInInstitution = true;
                    break;
                }
            }
            if (!isInInstitution) {
                filteredThemes.add(x);
            }
        }

        return filteredThemes;
    }

}
