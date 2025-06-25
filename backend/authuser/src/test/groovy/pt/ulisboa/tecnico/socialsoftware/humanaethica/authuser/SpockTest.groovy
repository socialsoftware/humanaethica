package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser

import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.AuthPasswordDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.UserApplicationalService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.repository.InstitutionRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.Mailer
import spock.lang.Specification

import java.time.LocalDateTime

@ActiveProfiles("test")
class SpockTest extends Specification {
    // remote requests

    WebClient webClient
    HttpHeaders headers

    // send email mocking

    @Value('${spring.mail.username}')
    public String mailerUsername

    @Autowired
    Mailer mailer

    // dates

    public static final LocalDateTime THREE_DAYS_AGO = DateHandler.now().minusDays(3)
    public static final LocalDateTime TWO_DAYS_AGO = DateHandler.now().minusDays(2)
    public static final LocalDateTime ONE_DAY_AGO = DateHandler.now().minusDays(1)
    public static final LocalDateTime NOW = DateHandler.now()
    public static final LocalDateTime IN_ONE_DAY = DateHandler.now().plusDays(1)
    public static final LocalDateTime IN_TWO_DAYS = DateHandler.now().plusDays(2)
    public static final LocalDateTime IN_THREE_DAYS = DateHandler.now().plusDays(3)

    // institution

    public static final String INSTITUTION_1_EMAIL = "institution1@mail.com"
    public static final String INSTITUTION_1_NAME = "institution1"
    public static final String INSTITUTION_1_NIF = "123456789"

    @Autowired
    InstitutionService institutionService

    @Autowired
    InstitutionRepository institutionRepository

    def createInstitution(String name, String email, String nif) {
        def institution = new Institution(name, email, nif)
        institutionRepository.save(institution)
        return institution
    }

    // login and demo

    public static final String ROLE_VOLUNTEER = "ROLE_VOLUNTEER"
    public static final String ROLE_MEMBER = "ROLE_MEMBER"
    public static final String ROLE_ADMIN = "ROLE_ADMIN"

    public static final String USER_1_NAME = "User 1 Name"
    public static final String USER_2_NAME = "User 2 Name"
    public static final String USER_3_NAME = "User 3 Name"
    public static final String USER_1_USERNAME = "rfs"
    public static final String USER_2_USERNAME = "jps"
    public static final String USER_3_USERNAME = "amm"
    public static final String USER_1_EMAIL = "user1@mail.com"
    public static final String USER_2_EMAIL = "user2@mail.com"
    public static final String USER_3_EMAIL = "user3@mail.com"
    public static final String USER_1_PASSWORD = "1234@WS4544"
    public static final String USER_2_PASSWORD = "4321@7877578"
    public static final String USER_3_PASSWORD = "4321@7877579"
    public static final String USER_1_TOKEN = "1a2b3c"
    public static final String USER_2_TOKEN = "c3b2a1"

    @Autowired
    AuthUserService authUserService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    AuthUserRepository authUserRepository

    /*@Autowired
    UserApplicationalService userServiceApplicational*/

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    DemoService demoService;

    @Autowired
    DemoUtils demoUtils



    /*def createMember(name, userName, password, email, type, institution, state) {
        def member = new Member(name, userName, email, institution, state)
        member = userRepository.save(member)
        def authUser = AuthUser.createAuthUser(member.getId(), userName, email, type, Role.MEMBER)
        authUser.setPassword(passwordEncoder.encode(password))
        authUserRepository.save(authUser)
        return member
    }

    def createVolunteer(name, userName, email, type, state) {
        def volunteer = new Volunteer(name, userName, email, state)
        volunteer= userRepository.save(volunteer)
        def authUser = AuthUser.createAuthUser(volunteer.getId(), userName, email, type, Role.VOLUNTEER)
        authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        authUserRepository.save(authUser)
        return volunteer
    }*/



    // clean database

    def deleteAll() {
        authUserRepository.deleteAll()
        userRepository.deleteAll()
        institutionRepository.deleteAll()

    }


}
