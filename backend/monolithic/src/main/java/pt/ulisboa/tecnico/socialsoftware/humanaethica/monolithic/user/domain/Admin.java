package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State;

@Entity
@DiscriminatorValue(User.UserTypes.ADMIN)
public class Admin extends User {
    public Admin() {
    }

    public Admin(String name, String username, String email, State state) {
        super(name, username, email, Role.ADMIN, state);
    }

    public Admin(String name, State state) {
        super(name, Role.ADMIN, state);
    }
}
