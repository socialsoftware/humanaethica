package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;


public class ThemeService {

    /*@Autowired
    ThemeRepository themeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstitutionRepository institutionRepository;

    /*public ThemeDto createTheme(String name, Integer userId){
        User user = getUsers(userId);
        if (user.getRole == ADMIN){
            Theme theme = new Theme();
            theme.setName(name);
        }
    }/*
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
