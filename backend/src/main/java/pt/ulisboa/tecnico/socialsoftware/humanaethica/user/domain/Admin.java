package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(User.UserTypes.ADMIN)
public class Admin extends User {
    public Admin() {
    }

    public Admin(String name, String username, String email, AuthUser.Type type) {
        super(name, username, email, Role.ADMIN, type);
    }

    public Admin(String name) {
        super(name, Role.ADMIN);
    }
}
