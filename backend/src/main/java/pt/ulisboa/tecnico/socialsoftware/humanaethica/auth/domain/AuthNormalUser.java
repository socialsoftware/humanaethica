package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;


@Entity
@DiscriminatorValue("EXTERNAL")
public class AuthNormalUser extends AuthUser {

    @Column(columnDefinition = "boolean default false")
    private boolean active;

    private String confirmationToken = "";

    private LocalDateTime tokenGenerationDate;

    @Override
    public boolean isDemo() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.NORMAL;
    }

    public AuthNormalUser() {
    }

    public AuthNormalUser(User user, String username, String email) {
        super(user, username, email);
        setActive(false);
        checkRole(isActive());
        generateConfirmationToken();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
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

    public void confirmRegistration(PasswordEncoder passwordEncoder, String confirmationToken, String password) {
        checkConfirmationToken(confirmationToken);
        setPassword(passwordEncoder.encode(password));
        setActive(true);
    }

    public void checkConfirmationToken(String token) {
        if (isActive()) {
            throw new HEException(USER_ALREADY_ACTIVE, getUsername());
        }
        if (!token.equals(getConfirmationToken()))
            throw new HEException(INVALID_CONFIRMATION_TOKEN);
        if (getTokenGenerationDate().isBefore(DateHandler.now().minusDays(1)))
            throw new HEException(EXPIRED_CONFIRMATION_TOKEN);
    }

    public String generateConfirmationToken() {
        String token = KeyGenerators.string().generateKey();
        setTokenGenerationDate(DateHandler.now());
        setConfirmationToken(token);
        return token;
    }


}
