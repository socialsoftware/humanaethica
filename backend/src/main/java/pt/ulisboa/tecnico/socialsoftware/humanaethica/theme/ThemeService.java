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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;

import java.util.Comparator;
import java.util.List;

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
                .map(ThemeDto::new)
                .sorted(Comparator.comparing(ThemeDto::getName))
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
    public void addInstitution(Integer themeId, List<Institution> institutions) {
        if (themeId == null) {
            throw new HEException(THEME_NOT_FOUND);
        }
        if (institutions.isEmpty()) {
            throw new HEException(EMPTY_INSTITUTION_LIST);
        }
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        for (Institution institution : institutions) {
            institution.addTheme(theme);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeInstitution(Integer themeId, List<Institution> institutions) {
        if (themeId == null) {
            throw new HEException(THEME_NOT_FOUND);
        }
        if (institutions.isEmpty()) {
            throw new HEException(EMPTY_INSTITUTION_LIST);
        }
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        for(Institution institution : institutions) {
            institution.removeTheme(theme);
        }
    }
}
