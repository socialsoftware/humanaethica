package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(User.UserTypes.MEMBER)
public class Member extends User {
    public Member() {
    }

    public Member(String name, String username, String email, AuthUser.Type type) {
        super(name, username, email, Role.MEMBER, type);
    }

}
