package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@DataJpaTest
class RegisterUserTest extends SpockTest {
    def userDto
    def previousNumberUser

    @Autowired
    Mailer mailerMock

    def setup() {
        previousNumberUser = userRepository.findAll().size()
    }

    def "the username does not exist, create the user"() {
        given: "an user dto"
        userDto = new RegisterUserDto()
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        userDto.setRole(User.Role.VOLUNTEER)

        when:
        def result = userServiceApplicational.registerUser(userDto)

        then: "the user is saved in the database"
        userRepository.findAll().size() == previousNumberUser + 1
        and: "checks if user data is correct"
        result.getUsername() == USER_1_USERNAME
        result.getEmail() == USER_1_EMAIL
        !result.getActive()
        and: "has confirmation tokem"
        result.getConfirmationToken() != ""
        and: "a mail is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_1_EMAIL, Mailer.QUIZZES_TUTOR_SUBJECT + userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, _)
    }

    def "the user exists"() {
        given: "a user"
        def user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL)
        userRepository.save(user)
        ((AuthNormalUser) user.authUser).setActive(true)
        and: "an user dto"
        userDto = new RegisterUserDto()
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        userDto.setRole(User.Role.VOLUNTEER)

        when:
        def result = userServiceApplicational.registerUser(userDto)

        then: "the user is saved in the database"
        userRepository.count() == previousNumberUser + 1
        and: "checks if user data is correct"
        result.getUsername() == USER_1_USERNAME
        result.getEmail() == USER_1_EMAIL
        result.getActive()
        and: "has token"
        result.getConfirmationToken() != ""

        and: "no mail is sent"
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
        userServiceApplicational.registerUser(userDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no user was created"
        userRepository.count() == previousNumberUser
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)

        where:
        username        | email           | role                || errorMessage
        null            | USER_1_EMAIL    | User.Role.VOLUNTEER || ErrorMessage.INVALID_AUTH_USERNAME
        "  "            | USER_1_EMAIL    | User.Role.VOLUNTEER || ErrorMessage.INVALID_AUTH_USERNAME
        USER_1_USERNAME | null            | User.Role.VOLUNTEER || ErrorMessage.INVALID_EMAIL
        USER_1_USERNAME | ""              | User.Role.VOLUNTEER || ErrorMessage.INVALID_EMAIL
        USER_1_USERNAME | "test.mail.com" | User.Role.VOLUNTEER || ErrorMessage.INVALID_EMAIL
        USER_1_USERNAME | "test@"         | User.Role.VOLUNTEER || ErrorMessage.INVALID_EMAIL
        USER_1_USERNAME | USER_1_EMAIL    | null                || ErrorMessage.INVALID_ROLE
        USER_1_USERNAME | USER_1_EMAIL    | User.Role.ADMIN     || ErrorMessage.INVALID_ROLE
        USER_1_USERNAME | USER_1_EMAIL    | User.Role.ADMIN     || ErrorMessage.INVALID_ROLE

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