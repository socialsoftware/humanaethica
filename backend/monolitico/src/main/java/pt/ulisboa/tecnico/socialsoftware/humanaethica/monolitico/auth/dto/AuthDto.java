package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.auth.dto;

public class AuthDto {
    private String token;

    private AuthUserDto user;

    public AuthDto() {}

    public AuthDto(String token, AuthUserDto user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthUserDto getUser() {
        return user;
    }

    public void setUser(AuthUserDto user) {
        this.user = user;
    }
}