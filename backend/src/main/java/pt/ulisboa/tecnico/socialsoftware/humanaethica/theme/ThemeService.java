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
import java.util.stream.Collectors;

@Service
public class ThemeService {

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstitutionRepository institutionRepository;

    /*@Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> getThemes() {
        return themeRepository.findAll().stream()
                .map(theme->new ThemeDto(theme,false,false))
                .sorted(Comparator.comparing(ThemeDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }*/
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> getThemes() {
        List<Theme> allThemes = themeRepository.findAll();
        List<ThemeDto> sortedThemes = new ArrayList<>();

        // Encontra os temas raiz (aqueles que não possuem um tema pai)
        List<Theme> rootThemes = allThemes.stream()
                .filter(theme -> theme.getParentTheme() == null)
                .toList();

        // Ordena os temas pela ordem da árvore
        for (Theme rootTheme : rootThemes) {
            traverseAndSortThemes(rootTheme, sortedThemes);
        }

        return sortedThemes;
    }

    private void traverseAndSortThemes(Theme theme, List<ThemeDto> sortedThemes) {
        // Cria um ThemeDto para o tema atual e adiciona à lista de temas ordenados
        sortedThemes.add(new ThemeDto(theme, false, false));

        // Ordena os subtemas do tema atual
        List<Theme> sortedSubThemes = theme.getSubThemes().stream()
                .sorted(Comparator.comparing(Theme::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        // Percorre recursivamente os subtemas e os adiciona à lista de temas ordenados
        for (Theme subTheme : sortedSubThemes) {
            traverseAndSortThemes(subTheme, sortedThemes);
        }
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
        Theme themeParent = null;
        if (themeDto.getParentTheme() != null) {
            themeParent = themeRepository.findById(themeDto.getParentTheme().getId()).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeDto.getParentTheme().getId())));
        }
        Theme theme;

        if (isAdmin){
            theme = new Theme(themeDto.getName(), Theme.State.APPROVED, themeParent);
        }
        else{
            theme = new Theme(themeDto.getName(), Theme.State.SUBMITTED, themeParent);
        }

        themeRepository.save(theme);
        return theme;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> deleteTheme(int themeId) {
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));
        if(!removeTheme(theme)){
            throw new HEException(THEME_CAN_NOT_BE_DELETED);
        };
        return getThemes();
    }

    public boolean removeTheme(Theme theme) {
        if (!theme.getInstitutions().isEmpty()){
            return false;
        }
        // Percorre recursivamente os subtemas e verifica se possuem instituições associadas
        for (Theme subTheme : theme.getSubTheme()) {
            if (hasAssociatedInstitutions(subTheme)) {
                return false;
            }
        }

        // Remove o subTheme atual da lista de subtemas do parentTheme
        //theme.getParentTheme().getSubTheme().remove(theme);
        theme.delete();
        // Percorre recursivamente os subtemas e remove-os
        for (Theme subTheme : theme.getSubTheme()) {
            subTheme.delete();
            removeTheme(subTheme);
        }

        return true;
    }

    public boolean hasAssociatedInstitutions(Theme theme) {
        // Verifica se o tema atual possui instituições associadas
        if (!theme.getInstitutions().isEmpty()) {
            return true;
        }

        // Percorre recursivamente os subtemas e verifica se possuem instituições associadas
        for (Theme subTheme : theme.getSubTheme()) {
            if (hasAssociatedInstitutions(subTheme)) {
                return true;
            }
        }

        return false;
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
        InstitutionDto institutionDto = new InstitutionDto(institution,false, false);
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
