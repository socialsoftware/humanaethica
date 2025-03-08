package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.InstitutionProfile;

import java.util.List;

@Repository
@Transactional
public interface InstitutionProfileRepository extends JpaRepository<InstitutionProfile, Integer> {
    @Query("SELECT p FROM InstitutionProfile p WHERE p.institution.id = :institutionId")
    List<InstitutionProfile> getProfileForInstitutionId(Integer institutionId);
}