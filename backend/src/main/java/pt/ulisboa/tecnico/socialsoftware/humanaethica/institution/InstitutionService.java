package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.LinkHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class InstitutionService {
    
    @Autowired
    InstitutionRepository institutionRepository;

    @Autowired
    private Mailer mailer;

    public static final String INSTITUTION_CONFIRMATION_MAIL_SUBJECT = "Institution Registration Confirmation";

    @Value("${spring.mail.username}")
    private String mailUsername;

    public Institution registerInstitution(InstitutionDto institutionDto) {

        if (institutionDto.getName() == null || institutionDto.getName().trim().length() == 0) {
            throw new HEException(INVALID_INSTITUTION_NAME, institutionDto.getName());
        }

        if (institutionDto.getEmail() == null || !institutionDto.getEmail().matches(UserService.MAIL_FORMAT)) {
            throw new HEException(INVALID_EMAIL, institutionDto.getEmail());
        }

        if (institutionDto.getNif() == null || institutionDto.getNif().length() != 9) {
            throw new HEException(INVALID_NIF, institutionDto.getEmail());
        }

        try {
            Integer.parseInt(institutionDto.getNif());
        } catch (NumberFormatException nfe) {
            throw new HEException(INVALID_NIF, institutionDto.getEmail());
        }

        if (institutionRepository.findInstitutionByNif(institutionDto.getNif()).isPresent()) {
            throw new HEException(NIF_ALREADY_EXIST, institutionDto.getNif());
        }

        Institution institution = new Institution(institutionDto.getName(), institutionDto.getEmail(), institutionDto.getNif());
        institutionRepository.save(institution);

        return institution;
    }

    public void validateInstitution(int id){
        Institution institution = institutionRepository.findById(id).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));
        institution.validate();
        sendConfirmationEmailTo(institution.getName(), institution.getEmail(), institution.generateConfirmationToken());
    }

    public void sendConfirmationEmailTo(String username, String email, String token) {
        mailer.sendSimpleMail(mailUsername, email, Mailer.QUIZZES_TUTOR_SUBJECT + INSTITUTION_CONFIRMATION_MAIL_SUBJECT, buildMailBody(username, token));
    }

    private String buildMailBody(String name, String token) {
        String msg = "To confirm the registration of the institution " + name + ", click the following link";
        return String.format("%s: %s", msg, LinkHandler.createConfirmRegistrationLink(name, token));
    }
}
