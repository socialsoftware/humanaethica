package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.*;

@Entity
@Table(name = "auth_users",
        indexes = {
                @Index(name = "auth_users_indx_0", columnList = "username")
        })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "auth_type",
        discriminatorType = DiscriminatorType.STRING)
public abstract class AuthUser implements UserDetails {
    public abstract boolean isDemo();

    public abstract Type getType();



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean active = true;

    @Column(name = "user_id", unique = true)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;

    private String password;

    @Column(unique = true)
    private String username;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    private String name;

    protected AuthUser() {
    }

    protected AuthUser(Integer user_id, String username, String email,Role role, String name) {
        setUser(user_id);
        setUsername(username);
        setEmail(email);
        setRole(role);
        setName(name);
    }

    public static AuthUser createAuthUser(Integer userId, String username, String email, Type type, Role role, String name) {
        switch (type) {
            case NORMAL:
                return new AuthNormalUser(userId, username, email, role, name);
            case DEMO:
                return new AuthDemoUser(userId, username, email, role, name);
            default:
                throw new HEException(INVALID_TYPE_FOR_AUTH_USER);
        }
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserID() {
        return userId;
    }

    public void setUser(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.toLowerCase() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches(UserService.MAIL_FORMAT))
            throw new HEException(INVALID_EMAIL, email);

        this.email = email.toLowerCase();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void checkRole(boolean isActive) {
        if (!isActive &&
                !(getRole().equals(Role.ADMIN) ||
                        getRole().equals(Role.VOLUNTEER) ||
                        getRole().equals(Role.MEMBER))) {
            throw new HEException(INVALID_ROLE, getRole().toString());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + getRole()));

        return list;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /*public void remove() {
        user.setAuthUser(null);
        setUser(null);
    }*/
}