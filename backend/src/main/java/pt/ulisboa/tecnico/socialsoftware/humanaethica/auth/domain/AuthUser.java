package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

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

    public enum Type {NORMAL, DEMO}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean active = true;

    @OneToOne
    private User user;

    private String email;

    private String password;

    @Column(unique = true)
    private String username;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    protected AuthUser() {
    }

    protected AuthUser(User user, String username, String email) {
        setUser(user);
        setUsername(username);
        setEmail(email);
    }

    public static AuthUser createAuthUser(User user, String username, String email, Type type) {
        switch (type) {
            case NORMAL:
                return new AuthNormalUser(user, username, email);
            case DEMO:
                return new AuthDemoUser(user, username, email);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                !(user.getRole().equals(User.Role.ADMIN) ||
                        user.getRole().equals(User.Role.VOLUNTEER) ||
                        user.getRole().equals(User.Role.MEMBER))) {
            throw new HEException(INVALID_ROLE, user.getRole().toString());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return list;
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

    public void remove() {
        user.setAuthUser(null);
        setUser(null);
    }
}