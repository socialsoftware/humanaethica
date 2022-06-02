package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import spock.mock.DetachedMockFactory


@DataJpaTest
class RegisterInstitutionTest extends SpockTest {
    def institutionDto

    @Autowired
    Mailer mailerMock

    def setup() {

    }

    def "create new institution"() {
        given: "an institution dto"
        institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)

        when:
        def result = institutionService.registerInstitution(institutionDto)

        then: "the institution is saved in the database"
        institutionRepository.findAll().size() == 1
        def institution = institutionRepository.findAll().get(0)
        institution.getConfirmationToken() != null
        and: "checks if institution data is correct"
        result.getName() == INSTITUTION_1_NAME
        result.getEmail() == INSTITUTION_1_EMAIL
        !result.isValid()
        and: "a mail is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, INSTITUTION_1_EMAIL, Mailer.QUIZZES_TUTOR_SUBJECT + institutionService.INSTITUTION_CONFIRMATION_MAIL_SUBJECT, _)
    }

    /*def "the user exists"() {
        given:
        def user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL)
        userRepository.save(user)
        ((AuthNormalUser) user.authUser).setActive(true)
        and:
        userDto = new RegisterUserDto()
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        userDto.setRole(User.Role.VOLUNTEER)

        when:
        userServiceApplicational.registerUser(userDto)

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
        userServiceApplicational.registerUser(userDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no user was created"
        userRepository.count() == 0
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
    }*/

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {
        def mockFactory = new DetachedMockFactory()

        @Bean
        Mailer mailer() {
            return mockFactory.Mock(Mailer)
        }
    }
}