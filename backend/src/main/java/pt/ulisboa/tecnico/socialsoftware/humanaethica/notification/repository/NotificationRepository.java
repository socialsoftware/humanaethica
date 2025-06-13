package pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.domain.Notification;

@Repository
@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}