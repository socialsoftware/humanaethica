package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {

    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type) {
        super(name, username, email, Role.VOLUNTEER, type);
    }

    public Volunteer(String name) {
        super(name, Role.VOLUNTEER);
    }
}
