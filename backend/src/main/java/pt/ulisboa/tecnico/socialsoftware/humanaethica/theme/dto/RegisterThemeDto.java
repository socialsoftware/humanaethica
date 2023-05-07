package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;

public class RegisterThemeDto {

    private String name;
    private Theme.State state;

    public RegisterThemeDto() {}
    public RegisterThemeDto(String name, Theme.State state) {
        this.name = name;
        this.state = state;
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public Theme.State getState() {return state;}

    public void setState(Theme.State state) {
        this.state = state;
    }


}