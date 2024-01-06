package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.InstitutionDocument;

@Repository
@Transactional
public interface DocumentRepository extends JpaRepository<InstitutionDocument, Long> {}