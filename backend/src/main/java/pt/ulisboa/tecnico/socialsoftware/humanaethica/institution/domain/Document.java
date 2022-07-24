package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain;


import javax.persistence.*;
@Entity
public class Document{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    byte[] content;

    String name;

    @OneToOne
    @JoinColumn(name="institution_id")
    private Institution institution;

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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }
}
