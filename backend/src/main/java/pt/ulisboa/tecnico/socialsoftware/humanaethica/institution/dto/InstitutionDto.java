package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;

public class InstitutionDto {
    private Integer id;

    public void setName(String name) {
        this.name = name;
    }

    private String email;

    private String name;

    public InstitutionDto(){
    }

    public InstitutionDto(Institution institution){
        this.id = institution.getId();
        this.email = institution.getEmail();
        this.name = institution.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
