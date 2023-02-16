package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {

    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }
}
