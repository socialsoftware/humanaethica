package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@DataJpaTest
class ConfirmRegistrationTest extends SpockTest {
    def externalUserDto
    def authUser

    @Autowired
    Mailer mailerMock

    def setup() {
        externalUserDto = new RegisterUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setUsername(USER_1_EMAIL)
        externalUserDto.setConfirmationToken(USER_1_TOKEN)
        externalUserDto.setRole(User.Role.VOLUNTEER)

        userService.registerUser(externalUserDto, null)

        authUser = authUserRepository.findAuthUserByUsername(USER_1_EMAIL).get()
        authUser.setConfirmationToken(USER_1_TOKEN)
    }

    def "user confirms registration successfully"() {
        given: "a password"
        externalUserDto.setPassword(USER_1_PASSWORD)

        when:
        def result = userServiceApplicational.confirmRegistration(externalUserDto)

        then: "the user has a new password and matches"
        passwordEncoder.matches(USER_1_PASSWORD, result.getPassword())
        and: "and is active"
        result.isActive()
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
    }

    def "user is already active"() {
        given: "an active user"
        externalUserDto.setPassword(USER_1_PASSWORD)
        authUser.setActive(true)

        when:
        userServiceApplicational.confirmRegistration(externalUserDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_ALREADY_ACTIVE
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
    }

    def "user token expired"() {
        given: "a new password"
        externalUserDto.setPassword(USER_1_PASSWORD)
        and: "and an expired token generation date"
        AuthUser authUser = authUserRepository.findAuthUserByUsername(USER_1_EMAIL).get()
        authUser.setTokenGenerationDate(DateHandler.now().minusDays(1).minusMinutes(1))

        when:
        def result = userServiceApplicational.confirmRegistration(externalUserDto)

        then:
        !result.active
        and: "a new token is created"
        result.confirmationToken != USER_1_TOKEN
        and: "a new confirmation mail is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_1_EMAIL, Mailer.HUMANAETHICA_SUBJECT + userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, _)
    }

    @Unroll
    def "registration confirmation unsuccessful"() {
        given: "a user"
        externalUserDto = new RegisterUserDto()
        externalUserDto.setEmail(email)
        externalUserDto.setUsername(email)
        externalUserDto.setConfirmationToken(token)
        externalUserDto.setPassword(password)

        when:
        userServiceApplicational.confirmRegistration(externalUserDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)

        where:
        email        | password        | token        || errorMessage
        ""           | USER_1_PASSWORD | USER_1_TOKEN || ErrorMessage.USER_NOT_FOUND
        null         | USER_1_PASSWORD | USER_1_TOKEN || ErrorMessage.USER_NOT_FOUND
        USER_1_EMAIL | null            | USER_1_TOKEN || ErrorMessage.INVALID_PASSWORD
        USER_1_EMAIL | ""              | USER_1_TOKEN || ErrorMessage.INVALID_PASSWORD
        USER_1_EMAIL | USER_1_PASSWORD | ""           || ErrorMessage.INVALID_CONFIRMATION_TOKEN
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
