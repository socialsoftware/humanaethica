package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;

@Entity
@DiscriminatorValue("DEMO")
public class AuthDemoUser extends AuthUser {
    @Override
    public boolean isDemo() {
        return true;
    }

    @Override
    public Type getType() {
        return Type.DEMO;
    }

    public AuthDemoUser() {
    }

    public AuthDemoUser(User user, String username, String email) {
        super(user, username, email);
    }
}
