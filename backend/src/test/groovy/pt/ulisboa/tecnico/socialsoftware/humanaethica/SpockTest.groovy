package pt.ulisboa.tecnico.socialsoftware.humanaethica

import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserApplicationalService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import spock.lang.Specification

import java.time.LocalDateTime

class SpockTest extends Specification {

    @Value('${spring.mail.username}')
    public String mailerUsername

    public static final String USER_1_NAME = "User 1 Name"
    public static final String USER_2_NAME = "User 2 Name"
    public static final String USER_3_NAME = "User 3 Name"

    public static final String USER_1_USERNAME = "ars"
    public static final String USER_2_USERNAME = "jps"
    public static final String USER_3_USERNAME = "amm"
    public static final String USER_1_EMAIL = "user1@mail.com"
    public static final String USER_2_EMAIL = "user2@mail.com"
    public static final String USER_3_EMAIL = "user3@mail.com"
    public final static String USER_1_PASSWORD = "1234@WS4544"
    public final static String USER_2_PASSWORD = "4321@7877578"
    public static final String USER_1_TOKEN = "1a2b3c"
    public static final String USER_2_TOKEN = "c3b2a1"

    public static final LocalDateTime LOCAL_DATE_BEFORE = DateHandler.now().minusDays(2)
    public static final LocalDateTime LOCAL_DATE_TODAY = DateHandler.now()
    
    public static final String ROLE_VOLUNTEER = "ROLE_VOLUNTEER"
    public static final String ROLE_MEMBER = "ROLE_MEMBER"
    public static final String ROLE_ADMIN = "ROLE_ADMIN"


    @Autowired
    AuthUserService authUserService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    AuthUserRepository authUserRepository

    @Autowired
    UserApplicationalService userServiceApplicational

    @Autowired
    DemoService demoService;

    @Autowired
    Mailer mailer

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    DemoUtils demoUtils

    RESTClient restClient

    def demoVolunteerLogin() {
        def loginResponse = restClient.get(
                path: '/auth/demo/volunteer'
        )
        restClient.headers['Authorization'] = "Bearer " + loginResponse.data.token
    }

    def demoMemberLogin() {
        def loginResponse = restClient.get(
                path: '/auth/demo/member'
        )
        restClient.headers['Authorization'] = "Bearer " + loginResponse.data.token
    }

    def normalUserLogin(username, password) {
        def loggedUser = restClient.get(
                path: '/auth/user',
                query: [
                        username: username,
                        password: password,
                ],
                requestContentType: 'application/json'
        )
        restClient.headers['Authorization'] = "Bearer " + loggedUser.data.token
    }

    def deleteAll() {
        authUserRepository.deleteAll()
        userRepository.deleteAll()
    }


}
