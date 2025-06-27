package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.VolunteerProfile;

import java.util.List;

@Repository
@Transactional
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Integer> {
    @Query("SELECT p FROM VolunteerProfile p WHERE p.volunteer.id = :userId")
    List<VolunteerProfile> getProfileForVolunteerId(Integer userId);
}