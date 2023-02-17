package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.RegisterInstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.Role;

public class RegisterUserDto {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private String password;
    private User.Role role;
    private boolean active;
    private boolean isInstitutionActive;
    private String confirmationToken;
    private int institutionId;

    public RegisterUserDto() {
    }

    public RegisterUserDto(AuthNormalUser authUser) {
        this.id = authUser.getId();
        this.name = authUser.getUser().getName();
        this.username = authUser.getUsername();
        this.email = authUser.getEmail();
        this.password = authUser.getPassword();
        this.role = authUser.getUser().getRole();
        if (this.role == Role.MEMBER)
            this.institutionId = ((Member) authUser.getUser()).getInstitution().getId();
        this.active = authUser.isActive();
        if (authUser.getUser() instanceof Member member) {
            this.isInstitutionActive = member.getInstitution().isActive();
        }
        this.confirmationToken = authUser.getConfirmationToken();
    }

    public RegisterUserDto(RegisterInstitutionDto registerInstitutionDto){
        this.name = registerInstitutionDto.getMemberName();
        this.username = registerInstitutionDto.getMemberUsername();
        this.email = registerInstitutionDto.getMemberEmail();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isInstitutionActive() {
        return isInstitutionActive;
    }

    public void setInstitutionActive(boolean institutionActive) {
        isInstitutionActive = institutionActive;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public int getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(int institutionId) {
        this.institutionId = institutionId;
    }
}
