package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto;

import java.io.Serializable;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Document;

public class DocumentDto implements Serializable {
    byte[] content;
    String name;

    public DocumentDto() {
    }

    public DocumentDto(Document document) {
        this.content = document.getContent();
        this.name = document.getName();
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
