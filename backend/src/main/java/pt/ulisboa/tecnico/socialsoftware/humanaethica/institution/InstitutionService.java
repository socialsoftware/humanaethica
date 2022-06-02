package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.LinkHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer;

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
        Institution institution = new Institution(institutionDto.getName(), institutionDto.getEmail());
        institutionRepository.save(institution);
        sendConfirmationEmailTo(institution.getName(), institution.getEmail(), institution.generateConfirmationToken());

        return institution;
    }

    public void sendConfirmationEmailTo(String username, String email, String token) {
        mailer.sendSimpleMail(mailUsername, email, Mailer.QUIZZES_TUTOR_SUBJECT + INSTITUTION_CONFIRMATION_MAIL_SUBJECT, buildMailBody(username, token));
    }

    private String buildMailBody(String name, String token) {
        String msg = "To confirm the registration of the institution " + name + ", click the following link";
        return String.format("%s: %s", msg, LinkHandler.createConfirmRegistrationLink(name, token));
    }
}
