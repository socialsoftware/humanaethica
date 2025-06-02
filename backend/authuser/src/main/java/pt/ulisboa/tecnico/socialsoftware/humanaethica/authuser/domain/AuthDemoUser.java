package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;

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

    public AuthDemoUser(Integer userId, String username, String email, Role role, String name) {
        super(userId, username, email, role, name);
    }
}
