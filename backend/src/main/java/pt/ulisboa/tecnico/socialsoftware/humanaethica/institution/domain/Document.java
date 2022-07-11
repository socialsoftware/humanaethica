package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.DocumentDto;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_URL_FOR_DOCUMENT;;

@Entity
@Table(name = "documents")
public class Document{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String url;

    @OneToOne
    @JoinColumn(name="institution_id")
    private Institution institution;

    public Document() {}

    public Document(DocumentDto documentDto) {
        setUrl(documentDto.getUrl());
    }

    public Document(String url) {
        setUrl(url);
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (url == null || url.isBlank())
            throw new HEException(INVALID_URL_FOR_DOCUMENT);
        this.url = url;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}