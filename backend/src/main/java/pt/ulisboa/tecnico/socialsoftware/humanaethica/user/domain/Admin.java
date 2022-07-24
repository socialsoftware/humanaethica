package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(User.UserTypes.ADMIN)
public class Admin extends User {
    public Admin() {
    }

    public Admin(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.ADMIN, type, state);
    }

    public Admin(String name, State state) {
        super(name, Role.ADMIN, state);
    }
}
