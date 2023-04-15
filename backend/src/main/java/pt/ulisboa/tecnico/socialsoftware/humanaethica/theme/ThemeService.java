package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository;

public class ThemeService {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    /*public ThemeDto createTheme(String name, Integer userId){
        User user = getUsers(userId);
        if (user.getRole == ADMIN){
            Theme theme = new Theme();
            theme.setName(name);
        }
    }
    public ThemeDto deleteTheme (Integer id, String name, ArrayList<Activity> activities, ArrayList<Institution> institutions){
        User user = getUsers(userId);
        if (user.getRole == ADMIN){

        }
    }
    public ThemeDto modifyTheme () {
        User user = getUsers(userId);
        if (user.getRole == ADMIN){

        }
    }*/
}
