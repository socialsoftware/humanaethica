package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution;

@Entity
@DiscriminatorValue(User.UserTypes.MEMBER)
public class Member extends User {

    @ManyToOne
    private Institution institution;

    public Member() {
    }

    public Member(String name, String username, String email, Institution institution, State state) {
        super(name, username, email, Role.MEMBER, state);
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
