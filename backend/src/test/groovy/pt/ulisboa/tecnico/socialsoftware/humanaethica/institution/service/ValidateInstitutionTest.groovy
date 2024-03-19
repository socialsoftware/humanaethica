package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import spock.mock.DetachedMockFactory

@DataJpaTest
class ValidateInstitutionTest extends SpockTest {
    def institutionDto
    def memberDto
    def institution
    def member

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

        member = userService.registerUser(memberDto, null);
    }

    def "validate institution with success"() {
        when:
        userServiceApplicational.validateUser(member.getId())
        institutionService.validateInstitution(institution.getId())

        then: "the institution and member are validated"
        institution.isActive()
        and: "an email is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_1_EMAIL, Mailer.HUMANAETHICA_SUBJECT + userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, _)
    }

    def "the institution doesn't exist"() {
        when:
        institutionService.validateInstitution(institution.getId() + 1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_NOT_FOUND
        and: "no email is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
    }

    def "the user haven't yet been approved"() {
        when:
        institutionService.validateInstitution(institution.getId())
        member.setState(User.State.SUBMITTED)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_APPROVED
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