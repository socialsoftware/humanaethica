package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll


@ContextConfiguration(classes = [AuthService, AuthUserRepository, UserService, PasswordEncoder])
class ValidateUserAuthServiceTest extends Specification{

    @SpringBean
    AuthUserRepository authUserRepository = Mock()

    @SpringBean
    UserService userService = Mock()

    @SpringBean
    PasswordEncoder passwordEncoder = Mock()

    @Autowired
    @Subject
    AuthService authService


    def authUser = Mock(AuthNormalUser)
    def user = Mock(User)

    def setup() {
        authUser.getUserID() >> 1
        authUser.getUsername() >> "USER_1_EMAIL"
        authUser.getEmail() >> "USER_1_EMAIL"
        //authUser.getRole() >> Role.VOLUNTEER
        authUser.getConfirmationToken() >> "token123"
        authUserRepository.findById(1) >> Optional.of(authUser)
    }

    @Unroll
    def "validate user with role=#role and institutionActive=#institutionActive"() {
        given:
        authUser.getUserID() >> 1
        authUser.getUsername() >> "USER_1_EMAIL"
        authUser.getEmail() >> "USER_1_EMAIL"
        authUser.getRole() >> role
        authUser.isActive() >> false

        authUserRepository.findById(1) >> Optional.of(authUser)
        userService.getUserState(1) >> State.SUBMITTED

        if (role == Role.MEMBER) {
            def institution = Mock(Institution)
            institution.isActive() >> institutionActive
            institution.getId() >> 42

            user = Mock(Member)
            user.getInstitution() >> institution
        } else {
            user = Mock(Volunteer)
        }

        userService.getUserById(1) >> user

        when:
        def result = authService.validateUser(1)

        then:
        1 * userService.changeState(1, State.APPROVED)
        result.getRole() == role
        if (role == Role.MEMBER) {
            result.isInstitutionActive() == institutionActive
        }

        where:
        role           | institutionActive
        Role.MEMBER    | false
        Role.MEMBER    | true
        Role.VOLUNTEER | null
    }


    def "validateUser - user does not exist"() {
        given:
        authUserRepository.findById(999) >> Optional.empty()

        when:
        authService.validateUser(999)

        then:
        def error = thrown(HEException)
        error.errorMessage == ErrorMessage.AUTHUSER_NOT_FOUND
    }

    def "validateUser - user is already active"() {
        given:
        authUser.isActive() >> true

        when:
        authService.validateUser(1)

        then:
        def error = thrown(HEException)
        error.errorMessage == ErrorMessage.USER_ALREADY_ACTIVE
    }

    def "validateUser - user state already active"() {
        given:
        authUser.isActive() >> false
        userService.getUserState(1) >> State.ACTIVE

        when:
        authService.validateUser(1)

        then:
        def error = thrown(HEException)
        error.errorMessage == ErrorMessage.USER_ALREADY_ACTIVE
    }

}
