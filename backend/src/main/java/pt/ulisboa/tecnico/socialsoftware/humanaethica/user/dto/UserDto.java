package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class UserDto {
    private Integer id;

    private String username;

    private String email;

    private String name;

    private String role;

    private String state;

    private boolean active;

    private String institutionName;

    private String type;

    private String creationDate;

    private String lastAccess;
    
    private boolean hasDocument;

    public UserDto() {
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.role = user.getRole().toString();
        this.state = user.getState().toString();
        this.creationDate = DateHandler.toISOString(user.getCreationDate());
        this.hasDocument = user.getDocument() != null;

        if (user.getAuthUser() != null) {
            this.active = user.getAuthUser().isActive();
            this.type = user.getAuthUser().getType().name();
            this.email = user.getAuthUser().getEmail();
            this.lastAccess = DateHandler.toISOString(user.getAuthUser().getLastAccess());
        }

        if (user.getRole().equals(Role.MEMBER)){
            this.institutionName = ((Member) user).getInstitution().getName();
        }

        else
            this.institutionName = null;
    }

    public UserDto(AuthUser authUser) {
        this(authUser.getUser());
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public boolean isHasDocument() {
        return hasDocument;
    }

    public void setHasDocument(boolean hasDocument) {
        this.hasDocument = hasDocument;
    }
}