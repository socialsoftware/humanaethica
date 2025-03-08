package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.InstitutionProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.InstitutionProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.repository.InstitutionProfileRepository;

import java.util.Optional;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class InstitutionProfileService {
    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private InstitutionProfileRepository institutionProfileRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<InstitutionProfileDto> getInstitutionProfile(Integer institutionId) {
        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        return institutionProfileRepository.getProfileForInstitutionId(institutionId).stream()
                .map(InstitutionProfileDto::new)
                .findFirst();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InstitutionProfileDto createInstitutionProfile(Integer institutionId,  InstitutionProfileDto institutionProfileDto) {
        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        if (institutionProfileDto == null) throw new HEException(INSTITUTION_PROFILE_SHORT_DESC_TOO_SHORT);

        InstitutionProfile institutionProfile = new InstitutionProfile(institution, institutionProfileDto);
        institutionProfileRepository.save(institutionProfile);

        return new InstitutionProfileDto(institutionProfile);
    }
}