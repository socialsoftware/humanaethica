package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT v FROM Volunteer v JOIN v.institutions i WHERE i.id = :institutionId")
    List<Volunteer> findVolunteersSubscribedToInstitution(Integer institutionId);
}