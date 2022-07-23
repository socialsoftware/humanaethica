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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import spock.mock.DetachedMockFactory
import spock.lang.Unroll


@DataJpaTest
class ValidateInstitutionTest extends SpockTest {
    def institutionDto
    def memberDto
    def institution

    @Autowired
    Mailer mailerMock

    def setup() {
        institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)
        institutionDto.setNif(INSTITUTION_1_NIF)

        institution = institutionService.registerInstitution(institutionDto)

        memberDto = new RegisterUserDto()
        memberDto.setEmail(USER_1_EMAIL)
        memberDto.setUsername(USER_1_EMAIL)
        memberDto.setConfirmationToken(USER_1_TOKEN)
        memberDto.setRole(User.Role.MEMBER)
        memberDto.setInstitutionId(institution.getId())

        userServiceApplicational.registerUser(memberDto);
    }

    def "validate institution with success"() {
        when:
        institutionService.validateInstitution(institution.getId())

        then: "the institution is validated"
        institution.isActive()
        and: "an email is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_1_EMAIL, Mailer.QUIZZES_TUTOR_SUBJECT + userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, _)
    }

    def "the institution doesn't exist"() {
        when:
        institutionService.validateInstitution(35)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_NOT_FOUND
        and: "no email is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
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