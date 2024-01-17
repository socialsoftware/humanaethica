package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

import java.util.Set;

@Repository
@Transactional
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    @Modifying
    @Query(value = "DELETE FROM activity_themes", nativeQuery = true)
    void deleteAllActivityTheme();
}