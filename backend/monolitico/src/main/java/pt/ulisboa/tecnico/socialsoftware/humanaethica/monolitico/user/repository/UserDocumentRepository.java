package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.user.domain.UserDocument;

@Repository
@Transactional
public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {}