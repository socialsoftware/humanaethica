package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto;

import java.io.Serializable;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Document;

public class DocumentDto implements Serializable {
    private Integer id;
    private String url;

    public DocumentDto() {
    }

    public DocumentDto(Document document) {
        this.id = document.getId();
        this.url = document.getUrl();
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DocumentDto{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
