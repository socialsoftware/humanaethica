package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.Mailer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@DataJpaTest
class RegisterUserTest extends SpockTest {
    def userDto

    @Autowired
    Mailer mailerMock

    def setup() {

    }

    def "the username does not exist, create the user"() {
        given: "an user dto"
        userDto = new RegisterUserDto()
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        userDto.setRole(Role.VOLUNTEER)

        when:
        def result = userService.registerUser(userDto, null)

        then: "the user is saved in the database"
        userRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getUsername() == USER_1_USERNAME
        result.getEmail() == USER_1_EMAIL
        !result.isActive()
        and: "a mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
    }

    def "the user exists"() {
        given:
        def user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,  State.SUBMITTED)
        userRepository.save(user)

        and:
        userDto = new RegisterUserDto()
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        userDto.setRole(Role.VOLUNTEER)

        when:
        userService.registerUser(userDto, null)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USERNAME_ALREADY_EXIST
        and:
        userRepository.count() == 1
        and:
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
    }

    @Unroll
    def "invalid arguments: username=#username | email=#email | role=#role"() {
        given: "an user dto"
        userDto = new RegisterUserDto()
        userDto.setUsername(username)
        userDto.setEmail(email)
        userDto.setRole(role)

        when:
        userService.registerUser(userDto, null)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no user was created"
        userRepository.count() == 0
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)

        where:
        username        | email           | role                || errorMessage
        null            | USER_1_EMAIL    | Role.VOLUNTEER      || ErrorMessage.INVALID_AUTH_USERNAME
        "  "            | USER_1_EMAIL    | Role.VOLUNTEER      || ErrorMessage.INVALID_AUTH_USERNAME
        USER_1_USERNAME | null            | Role.VOLUNTEER      || ErrorMessage.INVALID_EMAIL
        USER_1_USERNAME | ""              | Role.VOLUNTEER      || ErrorMessage.INVALID_EMAIL
        USER_1_USERNAME | "test.mail.com" | Role.VOLUNTEER      || ErrorMessage.INVALID_EMAIL
        USER_1_USERNAME | "test@"         | Role.VOLUNTEER      || ErrorMessage.INVALID_EMAIL
        USER_1_USERNAME | USER_1_EMAIL    | null                || ErrorMessage.INVALID_ROLE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {
        def mockFactory = new DetachedMockFactory()

        @Bean
        Mailer mailer() {
            return mockFactory.Mock(Mailer)
        }
    }
}