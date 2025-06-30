package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.INVALID_ROLE;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.INVALID_STATE;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type",
        discriminatorType = DiscriminatorType.STRING)
public abstract class User {


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


    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    private UserDocument document;

    private String username;

    private String email;

    protected User() {
    }

    protected User(String name, String username, String email, Role role, State state) {
        setName(name);
        setRole(role);
        setState(state);
        setEmail(email);
        setUsername(username);
        setCreationDate(DateHandler.now());
    }

    protected User(String name, Role role, State state) {
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
        return username;
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
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserDocument getDocument() {
        return document;
    }

    public void setDocument(UserDocument document) {
        this.document = document;
        document.setUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", role=" + role +
                ", username='" + getUsername() + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }

    public Institution remove() {
        this.setState(State.DELETED);
        if (this.role.equals(Role.MEMBER)) {
            return ((Member) this).getInstitution();
        } else
            return null;
    }
}