package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.activity.domain.Activity;

import java.util.List;

@Repository
@Transactional
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    @Query("SELECT a FROM Activity a WHERE a.institution.id = :institutionId")
    List<Activity> getActivitiesByInstitutionId(Integer institutionId);

    @Modifying
    @Query(value = "DELETE FROM activity_themes", nativeQuery = true)
    void deleteAllActivityTheme();


}