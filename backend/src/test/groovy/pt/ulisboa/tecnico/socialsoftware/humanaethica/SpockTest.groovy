package pt.ulisboa.tecnico.socialsoftware.humanaethica

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthPasswordDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserApplicationalService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.ActivityService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.ThemeService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import spock.lang.Specification

import java.time.LocalDateTime

class SpockTest extends Specification {
    @Value('${spring.mail.username}')
    public String mailerUsername

    public static final String ROLE_VOLUNTEER = "ROLE_VOLUNTEER"
    public static final String ROLE_MEMBER = "ROLE_MEMBER"
    public static final String ROLE_ADMIN = "ROLE_ADMIN"

    public static final String USER_1_NAME = "User 1 Name"
    public static final String USER_2_NAME = "User 2 Name"
    public static final String USER_3_NAME = "User 3 Name"
    public static final String USER_1_USERNAME = "ars"
    public static final String USER_2_USERNAME = "jps"
    public static final String USER_3_USERNAME = "amm"
    public static final String USER_1_EMAIL = "user1@mail.com"
    public static final String USER_2_EMAIL = "user2@mail.com"
    public static final String USER_3_EMAIL = "user3@mail.com"
    public static final String USER_1_PASSWORD = "1234@WS4544"
    public static final String USER_2_PASSWORD = "4321@7877578"
    public static final String USER_1_TOKEN = "1a2b3c"
    public static final String USER_2_TOKEN = "c3b2a1"

    public static final LocalDateTime TWO_DAYS_AGO = DateHandler.now().minusDays(2)
    public static final LocalDateTime ONE_DAY_AGO = DateHandler.now().minusDays(1)
    public static final LocalDateTime NOW = DateHandler.now()
    public static final LocalDateTime IN_ONE_DAY = DateHandler.now().plusDays(1)
    public static final LocalDateTime IN_TWO_DAYS = DateHandler.now().plusDays(2)
    public static final LocalDateTime IN_THREE_DAYS = DateHandler.now().plusDays(3)

    public static final String INSTITUTION_1_EMAIL = "institution1@mail.com"
    public static final String INSTITUTION_1_NAME = "institution1"
    public static final String INSTITUTION_1_NIF = "123456789"

    public static final String THEME_NAME_1 = "THEME_NAME 1"
    public static final String THEME_NAME_2 = "THEME_NAME 2"

    public static final String ACTIVITY_NAME_1 = "activity name 1"
    public static final String ACTIVITY_NAME_2 = "activity name 2"
    public static final String ACTIVITY_REGION_1 = "activity region 1"
    public static final String ACTIVITY_REGION_2 = "activity region 2"
    public static final String ACTIVITY_DESCRIPTION_1 = "activity description 1"
    public static final String ACTIVITY_DESCRIPTION_2 = "activity description 2"

    @Autowired
    AuthUserService authUserService

    @Autowired
    UserRepository userRepository

    @Autowired
    ThemeRepository themeRepository

    @Autowired
    UserService userService

    @Autowired
    AuthUserRepository authUserRepository

    @Autowired
    UserApplicationalService userServiceApplicational    

    @Autowired
    InstitutionService institutionService    

    @Autowired
    InstitutionRepository institutionRepository

    @Autowired
    ActivityRepository activityRepository

    @Autowired
    ActivityService activityService

    @Autowired
    ThemeService themeService

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

        return loginResponse.data.user
    }



    def demoMemberLogin() {
        def loginResponse = restClient.get(
                path: '/auth/demo/member'
        )
        restClient.headers['Authorization'] = "Bearer " + loginResponse.data.token

        return loginResponse.data.user
    }

    def demoAdminLogin() {
        demoService.getDemoAdmin()

        def loggedUser = restClient.post(
                path: '/auth/user',
                body: JsonOutput.toJson(new AuthPasswordDto("ars", "ars")),
                requestContentType: 'application/json'
        )
        restClient.headers['Authorization'] = "Bearer " + loggedUser.data.token

        return loggedUser.data.user
    }

    def normalUserLogin(username, password) {
        def loggedUser = restClient.post(
                path: '/auth/user',
                body: JsonOutput.toJson(new AuthPasswordDto(username, password)),
                requestContentType: 'application/json'
        )
        restClient.headers['Authorization'] = "Bearer " + loggedUser.data.token

        return loggedUser.data.user
    }

    def deleteAll() {
        activityRepository.deleteAllActivityTheme()
        activityRepository.deleteAll()
        authUserRepository.deleteAll()
        userRepository.deleteAll()
        institutionRepository.deleteAll()
        themeRepository.deleteAll()
    }


}
