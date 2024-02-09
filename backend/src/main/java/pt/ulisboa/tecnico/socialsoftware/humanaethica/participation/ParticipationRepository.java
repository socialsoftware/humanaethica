package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.List;

@Repository
@Transactional
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    @Query("SELECT p FROM Participation p WHERE p.activity.id = :activityId")
    List<Participation> getParticipationsByActivityId(Integer activityId);

    @Query("SELECT p FROM Participation p WHERE p.volunteer.id = :volunteerId")
    List<Participation> getParticipationsForVolunteerId(Integer volunteerId);
}
