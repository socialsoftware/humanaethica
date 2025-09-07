package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain;

public class AuxUser {

    Integer id;
    String username;
    String email;
    String role;
    boolean active;
    Integer institutionId;
    boolean institutionActive;


    public AuxUser(Integer id, String username, String email, String role, boolean active, Integer institutionId, boolean institutionActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.active = active;
        this.institutionId = institutionId;
        this.institutionActive = institutionActive;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public String getRole() {
        return role;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public boolean isInstitutionActive() {
        return institutionActive;
    }
}
