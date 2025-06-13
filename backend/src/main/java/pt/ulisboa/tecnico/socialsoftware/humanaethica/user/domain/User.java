package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_ROLE;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_STATE;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.domain.Notification;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type",
        discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    public enum State {SUBMITTED, APPROVED, ACTIVE, DELETED}

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

    @Enumerated(EnumType.STRING)
    private State state;

    private String name;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Notification> notifications = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public AuthUser authUser;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    private UserDocument document;

    protected User() {
    }

    protected User(String name, String username, String email, Role role, AuthUser.Type type, State state) {
        setName(name);
        setRole(role);
        setState(state);
        setAuthUser(AuthUser.createAuthUser(this, username, email, type));
        setCreationDate(DateHandler.now());
    }

    protected User(String name, User.Role role, State state) {
        setName(name);
        setRole(role);
        setState(state);
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (role == null)
            throw new HEException(INVALID_STATE);
        this.state = state;
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

    public UserDocument getDocument() {
        return document;
    }

    public void setDocument(UserDocument document) {
        this.document = document;
        document.setUser(this);
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public void deleteNotification(Notification notification) {
        notifications.remove(notification);
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

    public Institution remove() {
        this.setState(State.DELETED);
        this.getAuthUser().setActive(false);
        if (this.role.equals(Role.MEMBER)) {
            return ((Member) this).getInstitution();
        } else
            return null;
    }
}