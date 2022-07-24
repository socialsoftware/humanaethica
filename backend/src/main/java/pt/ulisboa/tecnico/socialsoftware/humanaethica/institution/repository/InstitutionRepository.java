package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;

@Repository
@Transactional
public interface InstitutionRepository extends JpaRepository<Institution, Integer> {
    @Query(value = "select * from institutions u where u.nif = lower(:nif)", nativeQuery = true)
    Optional<Institution> findInstitutionByNif(String nif);
}