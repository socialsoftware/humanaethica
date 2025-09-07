package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.dto;

public class UserAux {
    private final Integer id;
    private final String username;
    private final String email;
    private final String role;
    private final boolean active;
    private final Integer institutionId;
    private final boolean institutionActive;

    public UserAux(Integer id, String username, String email, String role, boolean active, Integer institutionId, boolean institutionActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.active = active;
        this.institutionId = institutionId;
        this.institutionActive = institutionActive;
    }

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public Integer getInstitutionId() { return institutionId; }
    public boolean isInstitutionActive() { return institutionActive; }

    public boolean isMember() {
        return "MEMBER".equalsIgnoreCase(role);
    }
}
