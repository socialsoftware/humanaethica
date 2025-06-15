package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;


public class AuthUserDto {
    private Integer id;
    private String username;
    private String email;
    private Role role;

    public AuthUserDto() {}

    public AuthUserDto(AuthUser authUser) {
        this.id = authUser.getUserID();
        this.username = authUser.getUsername();
        this.email = authUser.getEmail();
        this.role = authUser.getRole();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
