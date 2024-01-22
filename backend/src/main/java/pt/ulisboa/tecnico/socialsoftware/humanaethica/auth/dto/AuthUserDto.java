package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;

public class AuthUserDto {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private User.Role role;

    public AuthUserDto() {}

    public AuthUserDto(AuthUser authUser) {
        this.id = authUser.getUser().getId();
        this.name = authUser.getUser().getName();
        this.username = authUser.getUsername();
        this.email = authUser.getEmail();
        this.role = authUser.getUser().getRole();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
