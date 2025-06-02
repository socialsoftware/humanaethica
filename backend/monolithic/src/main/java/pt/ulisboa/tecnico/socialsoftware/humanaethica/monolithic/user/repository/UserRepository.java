package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "select * from users u where u.username = lower(:username)", nativeQuery = true)
    Optional<User> findUserByUsername(String username);
}