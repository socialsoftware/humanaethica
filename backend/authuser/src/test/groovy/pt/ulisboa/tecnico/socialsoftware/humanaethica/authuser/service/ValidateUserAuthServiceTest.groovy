package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuxUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll


@ContextConfiguration(classes = [AuthService])
class ValidateUserAuthServiceTest extends Specification{

    @SpringBean
    AuthUserRepository authUserRepository = Mock()

    @SpringBean
    AuthRemoteService userService = Mock()

    @SpringBean
    PasswordEncoder passwordEncoder = Mock()

    @Autowired
    @Subject
    AuthService authService


    def authUser = Mock(AuthNormalUser)
    def user = Mock(AuxUser)

    def setup() {
        authUser.getUserID() >> 1
        authUser.getUsername() >> "USER_1_EMAIL"
        authUser.getEmail() >> "USER_1_EMAIL"
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
        userService.getUserState(1) >> State.SUBMITTED.name()

        if (role == Role.MEMBER) {
            user.getInstitutionId() >> 42
            user.isInstitutionActive() >> institutionActive
        }

        userService.getUserByIdLogin(1) >> user

        when:
        def result = authService.validateUser(1)

        then:
        1 * userService.changeStateLogin(1, State.ACTIVE.name())
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
        userService.getUserState(1) >> State.ACTIVE.name()

        when:
        authService.validateUser(1)

        then:
        def error = thrown(HEException)
        error.errorMessage == ErrorMessage.USER_ALREADY_ACTIVE
    }

}
