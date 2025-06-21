package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT v FROM Volunteer v JOIN v.institutions i WHERE i.id = :institutionId")
    List<Volunteer> findVolunteersSubscribedToInstitution(Integer institutionId);

    @Query(value = "SELECT EXISTS ( " +
                   "SELECT 1 FROM institution_subscriptions " +
                   "WHERE volunteer_id = :volunteerId AND institution_id = :institutionId )",
           nativeQuery = true)
    boolean isVolunteerSubscribedToInstitution(Integer volunteerId, Integer institutionId);

    @Query("SELECT m FROM Member m WHERE m.institution.id = :institutionId")
    List<Member> findMembersByInstitutionId(Integer institutionId);

    @Query("SELECT a.volunteer FROM ActivitySuggestion a WHERE a.id = :suggestionId")
    Volunteer findVolunteerByActivitySuggestionId(Integer suggestionId);
}