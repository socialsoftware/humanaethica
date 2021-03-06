package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_ROLE;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.USER_IS_ACTIVE;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type",
        discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    public enum Role {VOLUNTEER, MEMBER, ADMIN}

    public static class UserTypes {
        public static final String VOLUNTEER = "VOLUNTEER";

        public static final String MEMBER = "MEMBER";

        public static final String ADMIN = "ADMIN";

        private UserTypes() {

        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public AuthUser authUser;

    protected User() {
    }

    protected User(String name, String username, String email, Role role, AuthUser.Type type) {
        setName(name);
        setRole(role);
        setAuthUser(AuthUser.createAuthUser(this, username, email, type));
        setCreationDate(DateHandler.now());
    }

    protected User(String name, User.Role role) {
        setName(name);
        setRole(role);
        setCreationDate(DateHandler.now());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        if (authUser == null) {
            return String.format("%s-%s", getRole().toString().toLowerCase(), getId());
        }
        return authUser.getUsername();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role == null)
            throw new HEException(INVALID_ROLE);

        this.role = role;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getEmail() {
        return authUser.getEmail();
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", role=" + role +
                ", username='" + getUsername() + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", lastAccess=" + authUser.getLastAccess() +
                '}';
    }

    public void remove() {
        if (getAuthUser() != null && !getAuthUser().isDemo() && getAuthUser().isActive()) {
            throw new HEException(USER_IS_ACTIVE, getUsername());
        }
    }
}