package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto;

public class RegisterThemeDto {

    private String name;

    public RegisterThemeDto() {}
    public RegisterThemeDto(String name) {
        this.name = name;
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }
}