package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;


@Repository
@Transactional
public interface ThemeRepository extends JpaRepository<Theme, Integer> {}