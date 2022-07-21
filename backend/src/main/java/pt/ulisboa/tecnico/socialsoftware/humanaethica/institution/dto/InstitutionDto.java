package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Document;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class InstitutionDto {
    private Integer id;

    private String email;

    private String name;

    private String nif;

    private boolean active;

    private String creationDate;

    private Document document;

    public InstitutionDto(){
    }

    public InstitutionDto(Institution institution){
        setId(institution.getId());
        setEmail(institution.getEmail());
        setName(institution.getName());
        setNif(institution.getNIF());
        setActive(institution.isActive());
        setCreationDate(DateHandler.toISOString(institution.getCreationDate()));
        setDocument(institution.getDocument());
    }

    public InstitutionDto(RegisterInstitutionDto registerInstitutionDto){
        setEmail(registerInstitutionDto.getInstitutionEmail());
        setName(registerInstitutionDto.getInstitutionName());
        setNif(registerInstitutionDto.getInstitutionNif());
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

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
