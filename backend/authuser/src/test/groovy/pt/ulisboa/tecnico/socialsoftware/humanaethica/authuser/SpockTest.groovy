package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser

import org.spockframework.spring.SpringBean
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.AuthRemoteService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.AuthService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.Mailer
import spock.lang.Specification

import java.time.LocalDateTime

@ActiveProfiles("test")
@DataJpaTest
@Import([
        TestBeans,
        AuthService,
        AuthUserService,
        DemoService,
        DemoUtils
])
abstract class SpockTest extends Specification {


    @SpringBean
    AuthRemoteService authRemoteService = Mock()

    @SpringBean
    JavaMailSender javaMailSender = Mock()

    /** Injeções comuns e úteis nos testes */
    @Autowired AuthUserRepository authUserRepository
    @Autowired PasswordEncoder passwordEncoder
    @Autowired(required = false) AuthUserService authUserService
    @Autowired(required = false) DemoService demoService
    @Autowired(required = false) DemoUtils demoUtils
    @Autowired Mailer mailer

    // dates

    public static final LocalDateTime THREE_DAYS_AGO = DateHandler.now().minusDays(3)
    public static final LocalDateTime TWO_DAYS_AGO = DateHandler.now().minusDays(2)
    public static final LocalDateTime ONE_DAY_AGO = DateHandler.now().minusDays(1)
    public static final LocalDateTime NOW = DateHandler.now()
    public static final LocalDateTime IN_ONE_DAY = DateHandler.now().plusDays(1)
    public static final LocalDateTime IN_TWO_DAYS = DateHandler.now().plusDays(2)
    public static final LocalDateTime IN_THREE_DAYS = DateHandler.now().plusDays(3)



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


    // clean database

    def deleteAll() {
        authUserRepository.deleteAll()
    }


    @TestConfiguration
    static class TestBeans {
        @Bean PasswordEncoder passwordEncoder() { new BCryptPasswordEncoder() }
        @Bean Mailer mailer() { new Mailer() }
    }


}
