package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.springframework.security.crypto.keygen.KeyGenerators;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

@Entity
public class Institution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String email;

    private boolean valid = false;

    private String confirmationToken = "";

    private LocalDateTime tokenGenerationDate;

    public boolean isValid() {
        return valid;
    }

    public Institution(){
    }

    public Institution(String name, String email){
        setEmail(email);
        setName(name);
        generateConfirmationToken();
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

    public void validate() {
        this.valid = true;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getTokenGenerationDate() {
        return tokenGenerationDate;
    }

    public void setTokenGenerationDate(LocalDateTime tokenGenerationDate) {
        this.tokenGenerationDate = tokenGenerationDate;
    }

    public String generateConfirmationToken() {
        String token = KeyGenerators.string().generateKey();
        setTokenGenerationDate(DateHandler.now());
        setConfirmationToken(token);
        return token;
    }
}
