package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

@Repository
@Transactional
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
}