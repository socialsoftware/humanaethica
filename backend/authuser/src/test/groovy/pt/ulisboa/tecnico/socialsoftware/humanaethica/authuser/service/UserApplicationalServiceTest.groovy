package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service

import org.spockframework.spring.SpringBean
import org.spockframework.spring.StubBeans
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.Mailer
import spock.lang.Specification
import spock.lang.Subject

@StubBeans([AuthService, Mailer])
@ContextConfiguration(classes = [UserApplicationalService, AuthService, Mailer])
class UserApplicationalServiceTest extends Specification {

    def registerUserDto

    @SpringBean
    AuthService authService = Mock()

    @SpringBean
    Mailer mailerMock = Mock()

    @Autowired
    @Subject
    UserApplicationalService service

    def setup() {
        registerUserDto = new RegisterUserDto()
        registerUserDto.setEmail("USER_1_EMAIL")
        registerUserDto.setUsername("USER_1_EMAIL")
        registerUserDto.setConfirmationToken("USER_1_TOKEN")
        registerUserDto.setRole(Role.VOLUNTEER)
    }

    def "confirmRegistration - user is NOT active - should send confirmation email"() {
        given:
        registerUserDto.setActive(false)

        when:
        def result = service.confirmRegistration(registerUserDto)

        then:
        1 * authService.confirmRegistration(registerUserDto) >> registerUserDto
        1 * mailerMock.sendSimpleMail(_, "USER_1_EMAIL", _, _)
        result == registerUserDto
    }

    def "confirmRegistration - user IS active - should NOT send confirmation email"() {
        given:
        registerUserDto.setActive(true)

        when:
        def result = service.confirmRegistration(registerUserDto)

        then:
        1 * authService.confirmRegistration(registerUserDto) >> registerUserDto
        0 * mailerMock.sendSimpleMail(_, _, _, _)
        result == registerUserDto
    }

    def "validateUser - user is not MEMBER - should send confirmation email"() {
        given:
        def userId = 1

        when:
        service.validateUser(userId)

        then:
        1 * authService.validateUser(userId) >> registerUserDto
        1 * mailerMock.sendSimpleMail(_, "USER_1_EMAIL", _, _)
    }

    def "validateUser - user is MEMBER and institution is not active - should NOT send email"() {
        given:
        def userId = 1
        registerUserDto.setRole(Role.MEMBER)
        registerUserDto.setInstitutionActive(false)

        when:
        service.validateUser(userId)

        then:
        1 * authService.validateUser(userId) >> registerUserDto
        0 * mailerMock.sendSimpleMail(_, _, _, _)
    }


    def "validateUser - user is MEMBER but institution is active - should send email"() {
        given:
        def userId = 1
        registerUserDto.setRole(Role.MEMBER)
        registerUserDto.setInstitutionActive(true)

        when:
        service.validateUser(userId)

        then:
        1 * authService.validateUser(userId) >> registerUserDto
        1 * mailerMock.sendSimpleMail(_, "USER_1_EMAIL", _, _)
    }



}
