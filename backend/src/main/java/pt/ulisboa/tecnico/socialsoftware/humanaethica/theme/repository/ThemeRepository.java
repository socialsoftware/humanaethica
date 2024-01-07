package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;

import java.util.List;


@Repository
@Transactional
public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    @Query(value = "SELECT theme FROM Theme theme WHERE theme.name = :name")
    List<Theme> getThemesByName(String name);
}