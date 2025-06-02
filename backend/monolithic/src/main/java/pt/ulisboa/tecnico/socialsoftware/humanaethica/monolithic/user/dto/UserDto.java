package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.dto;


import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User;

public class UserDto {
    private Integer id;

    private String username;

    private String email;

    private String name;

    private String role;

    private String state;

    private boolean active;

    private String institutionName;


    private String creationDate;
    
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
        this.email = user.getEmail();

        this.active = user.getState() == User.State.ACTIVE;

        if (user.getRole().equals(Role.MEMBER)){
            this.institutionName = ((Member) user).getInstitution().getName();
        }

        else
            this.institutionName = null;
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


    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
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