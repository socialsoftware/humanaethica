package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;

@Entity
@DiscriminatorValue(User.UserTypes.MEMBER)
public class Member extends User {

    @ManyToOne
    private Institution institution;

    public Member() {
    }

    public Member(String name, String username, String email, AuthUser.Type type, Institution institution, State state) {
        super(name, username, email, Role.MEMBER, type, state);
        setInstitution(institution);
        institution.addMember(this);
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

}
