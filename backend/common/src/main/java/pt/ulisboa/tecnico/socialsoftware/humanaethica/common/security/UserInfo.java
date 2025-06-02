package pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserInfo {

    private final Integer id;
    private final String username;
    private final String role;

    public UserInfo(Integer id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Integer getId() { return id; }
    public String getUsername() { return username; }

    public String getRole() { return role; }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }

    public boolean isAdmin() {
        return Objects.equals(role, "ADMIN");
    }

    public boolean isMember() {
        return Objects.equals(role, "MEMBER");
    }

    public boolean isVolunteer() {
        return Objects.equals(role, "VOLUNTEER");
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +

                ", role=" + role +

                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo userInfo)) return false;
        return Objects.equals(id, userInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
