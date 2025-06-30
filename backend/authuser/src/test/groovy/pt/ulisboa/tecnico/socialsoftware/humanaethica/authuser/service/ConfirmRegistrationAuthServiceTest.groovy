package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service


import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import spock.lang.Specification
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Subject
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State;
import spock.lang.Unroll

@ContextConfiguration(classes = [AuthService, AuthUserRepository, UserService, PasswordEncoder])
class ConfirmRegistrationAuthServiceTest extends Specification {

    @SpringBean
    AuthUserRepository authUserRepository = Mock()

    @SpringBean
    UserService userService = Mock()

    @SpringBean
    PasswordEncoder passwordEncoder = Mock()

    @Autowired
    @Subject
    AuthService authService

    def externalUserDto = new RegisterUserDto()
    def authUser = Mock(AuthNormalUser)
    def user = Mock(Volunteer)

    def setup() {
        externalUserDto.setEmail("USER_1_EMAIL")
        externalUserDto.setUsername("USER_1_EMAIL")
        externalUserDto.setConfirmationToken("1a2b3c")
        externalUserDto.setRole(Role.VOLUNTEER)

        authUserRepository.findAuthUserByUsername("USER_1_EMAIL") >> Optional.of(authUser)
        authUser.getUserID() >> 1
        authUser.getUsername() >> "USER_1_EMAIL"
        authUser.getEmail() >> "USER_1_EMAIL"
        authUser.getRole() >> Role.VOLUNTEER
        authUser.getConfirmationToken() >> "1a2b3c"
    }

    def "user confirms registration successfully"() {
        given:
        externalUserDto.setPassword("1234@WS4544")
        authUser.isActive() >> true
        passwordEncoder.matches("1234@WS4544", "encoded-password") >> true
        authUser.getPassword() >> "encoded-password"

        when:
        def result = authService.confirmRegistration(externalUserDto)

        then:
        1 * userService.changeState(authUser.getUserID(), State.ACTIVE)
        1 * userService.getUserById(authUser.getUserID()) >> user
        result.getPassword() == "encoded-password"
        result.isActive()
    }

    def "user is already active"() {
        given:
        externalUserDto.setPassword("1234@WS4544")
        authUser.isActive() >> true
        authUser.confirmRegistration(_ as PasswordEncoder, _ as String, _ as String) >> {
            throw new HEException(ErrorMessage.USER_ALREADY_ACTIVE)
        }

        when:
        authService.confirmRegistration(externalUserDto)

        then:
        0 * userService.changeState(_, _)
        0 * userService.getUserById(_)
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_ALREADY_ACTIVE
    }

    def "user token expired"() {
        given:
        externalUserDto.setPassword("1234@WS4544")
        authUser.isActive() >> false
        authUser.confirmRegistration(_ as PasswordEncoder, _ as String, _ as String) >> {
            throw new HEException(ErrorMessage.EXPIRED_CONFIRMATION_TOKEN)
        }

        when:
        def result = authService.confirmRegistration(externalUserDto)

        then:
        !result.isActive()
        1 * authUser.generateConfirmationToken()
        1 * userService.changeState(_, _)
        1 * userService.getUserById(_)
    }

    @Unroll
    def "registration confirmation unsuccessful"() {
        given:
        def dto = new RegisterUserDto()
        dto.setEmail(email)
        dto.setUsername(email)
        dto.setConfirmationToken(token)
        dto.setPassword(password)

        authUserRepository.findAuthUserByUsername(email) >> Optional.ofNullable(
                email == "USER_1_EMAIL" ? authUser : null
        )

        authUser.confirmRegistration(_ as PasswordEncoder, token, _ as String) >> {
            if (token == ""){
                throw new HEException(ErrorMessage.INVALID_CONFIRMATION_TOKEN)
            }
        }


        when:
        authService.confirmRegistration(dto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        email               | password        | token      || errorMessage
        ""                  | "valid-password"| "1a2b3c"   || ErrorMessage.USER_NOT_FOUND
        null                | "valid-password"| "1a2b3c"   || ErrorMessage.USER_NOT_FOUND
        "USER_1_EMAIL"      | null            | "1a2b3c"   || ErrorMessage.INVALID_PASSWORD
        "USER_1_EMAIL"      | ""              | "1a2b3c"   || ErrorMessage.INVALID_PASSWORD
        "USER_1_EMAIL"      | "valid-password"| ""         || ErrorMessage.INVALID_CONFIRMATION_TOKEN
    }
}
