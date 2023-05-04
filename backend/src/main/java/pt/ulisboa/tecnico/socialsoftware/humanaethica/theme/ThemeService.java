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
    public Theme registerTheme(ThemeDto themeDto) {

        if (themeDto.getName() == null || themeDto.getName().trim().length() == 0) {
            throw new HEException(INVALID_THEME_NAME, themeDto.getName());
        }

        Theme theme = new Theme(themeDto.getName());
        themeRepository.save(theme);

        return theme;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ThemeDto> deleteTheme(int themeId) {
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new HEException(THEME_NOT_FOUND,Integer.toString(themeId)));

        theme.delete();
        return getThemes();
    }
}
