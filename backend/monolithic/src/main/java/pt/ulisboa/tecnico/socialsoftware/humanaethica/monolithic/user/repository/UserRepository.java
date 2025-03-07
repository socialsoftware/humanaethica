package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
}