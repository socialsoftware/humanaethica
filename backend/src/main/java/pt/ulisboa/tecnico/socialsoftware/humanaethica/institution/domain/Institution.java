package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain;

import jakarta.persistence.*;
import org.springframework.security.crypto.keygen.KeyGenerators;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "institutions")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String email;

    private String nif;

    private boolean active = false;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    private String confirmationToken = "";

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "institution", fetch = FetchType.EAGER, orphanRemoval = true)
    private Document document;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "institution", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    private LocalDateTime tokenGenerationDate;

    public Institution() {
    }

    public Institution(String name, String email, String nif) {
        setEmail(email);
        setName(name);
        setNIF(nif);
        generateConfirmationToken();
        setCreationDate(DateHandler.now());
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public List<Member> getMembers() {
        return members;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNIF() {
        return nif;
    }

    public void setNIF(String nIF) {
        nif = nIF;
    }

    public void validate() {
        this.active = true;
    }

    public void delete() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getTokenGenerationDate() {
        return tokenGenerationDate;
    }

    public void setTokenGenerationDate(LocalDateTime tokenGenerationDate) {
        this.tokenGenerationDate = tokenGenerationDate;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
        document.setInstitution(this);
    }

    public String generateConfirmationToken() {
        String token = KeyGenerators.string().generateKey();
        setTokenGenerationDate(DateHandler.now());
        setConfirmationToken(token);
        return token;
    }
}
