package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoUtils;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.InstitutionDocument;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.RegisterInstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.DocumentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserApplicationalService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.LinkHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class InstitutionService {
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    ThemeRepository themeRepository;
    @Autowired
    private UserApplicationalService userApplicationalService;
    @Autowired
    private UserService userService;
    @Autowired
    private Mailer mailer;

    public static final String PASSWORD_CONFIRMATION_MAIL_SUBJECT = "Password Confirmation";

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<InstitutionDto> getInstitutions() {
        return institutionRepository.findAll().stream()
                .map(institution-> new InstitutionDto(institution,true,true))
                .sorted(Comparator.comparing(InstitutionDto::getName))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InstitutionDocument getInstitutionDocument(Integer id) {
        return (institutionRepository.findById(id).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND))).getDocument();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<InstitutionDto> deleteInstitution(int institutionId) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        institution.delete();

        for (User user : institution.getMembers()){
            user.remove();
        }

        return getInstitutions();
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void registerInstitutionAndMember(RegisterInstitutionDto registerInstitutionDto, MultipartFile institutionDocument, MultipartFile memberDocument) throws IOException {
        InstitutionDocument document = new InstitutionDocument();
        document.setName(institutionDocument.getName());
        document.setContent(institutionDocument.getBytes());

        InstitutionDto institutionDto = new InstitutionDto(registerInstitutionDto);

        Institution institution = registerInstitution(institutionDto);
        addDocument(institution, document);


        RegisterUserDto registerUserDto = new RegisterUserDto(registerInstitutionDto);
        registerUserDto.setRole(User.Role.MEMBER);

        registerUserDto.setInstitutionId(institution.getId());
        userService.registerUser(registerUserDto, memberDocument);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Institution registerInstitution(InstitutionDto institutionDto) {

        if (institutionDto.getName() == null || institutionDto.getName().trim().length() == 0) {
            throw new HEException(INVALID_INSTITUTION_NAME, institutionDto.getName());
        }

        if (institutionDto.getEmail() == null || !institutionDto.getEmail().matches(UserService.MAIL_FORMAT)) {
            throw new HEException(INVALID_EMAIL, institutionDto.getEmail());
        }

        if (institutionDto.getNif() == null || institutionDto.getNif().length() != 9) {
            throw new HEException(INVALID_NIF, institutionDto.getNif());
        }

        try {
            Integer.parseInt(institutionDto.getNif());
        } catch (NumberFormatException nfe) {
            throw new HEException(INVALID_NIF, institutionDto.getNif());
        }

        if (institutionRepository.findInstitutionByNif(institutionDto.getNif()).isPresent()) {
            throw new HEException(NIF_ALREADY_EXIST, institutionDto.getNif());
        }

        Institution institution = new Institution(institutionDto.getName(), institutionDto.getEmail(), institutionDto.getNif());

        institutionRepository.save(institution);

        return institution;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addDocument(Institution institution, InstitutionDocument doc) {
        institution.setDocument(doc);
        documentRepository.save(doc);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<InstitutionDto>  validateInstitution(int institutionId) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, institutionId));
        Member member = institution.getMembers().isEmpty() ? null : institution.getMembers().get(0);
        if (member != null && member.getState().equals(State.SUBMITTED))
            throw new HEException(USER_NOT_APPROVED);
        if (member != null && !institution.isActive())
            sendConfirmationEmailTo(member.getUsername(), member.getEmail(), institution.generateConfirmationToken());
        institution.validate();

        return getInstitutions();
    }

    public void sendConfirmationEmailTo(String username, String email, String token) {
        mailer.sendSimpleMail(mailUsername, email, Mailer.HUMANAETHICA_SUBJECT + PASSWORD_CONFIRMATION_MAIL_SUBJECT, buildMailBody(username, token));
    }

    private String buildMailBody(String username, String token) {
        String msg = "To confirm your registration, as external user using username (" + username + ") click the following link";
        return String.format("%s: %s", msg, LinkHandler.createConfirmRegistrationLink(username, token));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Institution getDemoInstitution() {
        return institutionRepository.findInstitutionByNif(DemoUtils.DEMO_INSTITUTION_NIF).orElseGet(() -> {
            Institution institution = new Institution(DemoUtils.DEMO_INSTITUTION, "demo_institution@mail.com", DemoUtils.DEMO_INSTITUTION_NIF);
            institution.validate();
            institutionRepository.save(institution);
            return institution;
        });
    }
}
