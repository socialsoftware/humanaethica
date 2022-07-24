package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto;

public class RegisterInstitutionDto {

    private String institutionEmail;

    private String institutionName;

    private String institutionNif;

    private String memberUsername;

    private String memberEmail;

    private String memberName;

    //porque e que tens duas funcoes com o mesmo nome?
    public RegisterInstitutionDto() {
    }

    public RegisterInstitutionDto(String institutionEmail, String institutionName, String institutionNif,
            String memberUsername, String memberEmail, String memberName) {
        this.institutionEmail = institutionEmail;
        this.institutionName = institutionName;
        this.institutionNif = institutionNif;
        this.memberUsername = memberUsername;
        this.memberEmail = memberEmail;
        this.memberName = memberName;
    }

    public String getInstitutionEmail() {
        return institutionEmail;
    }

    public void setInstitutionEmail(String institutionEmail) {
        this.institutionEmail = institutionEmail;
    }



    public String getInstitutionName() {
        return institutionName;
    }



    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }



    public String getInstitutionNif() {
        return institutionNif;
    }



    public void setInstitutionNif(String institutionNif) {
        this.institutionNif = institutionNif;
    }



    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
