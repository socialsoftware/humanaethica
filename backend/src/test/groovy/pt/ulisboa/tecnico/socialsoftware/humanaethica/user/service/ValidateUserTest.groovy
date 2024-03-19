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
class ValidateUserTest extends SpockTest {
    def institutionDto
    def institution
    def memberDto
    def member
    def volunteerDto

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

    def "validate member of not active institution"() {
        when:
        userServiceApplicational.validateUser(member.getId())

        then: "the institution and member are validated"
        def user = userRepository.findAll().get(0)
        user.getState().equals(User.State.APPROVED)
        and: "no email is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername, _, _, _)
    }

    def "validate member of active institution"() {
        given:
        institution.validate()
        
        when:
        userServiceApplicational.validateUser(member.getId())

        then: "the institution and member are validated"
        def user = userRepository.findAll().get(0)
        user.getState().equals(User.State.APPROVED)
        and: "an email is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_1_EMAIL, Mailer.HUMANAETHICA_SUBJECT + userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, _)
    }

    def "validate volunteer with success"() {
        given:
        volunteerDto = new RegisterUserDto()
        volunteerDto.setEmail(USER_2_EMAIL)
        volunteerDto.setUsername(USER_2_EMAIL)
        volunteerDto.setConfirmationToken(USER_2_TOKEN)
        volunteerDto.setRole(User.Role.VOLUNTEER)

        def volunteer = userService.registerUser(volunteerDto, null);

        when:
        userServiceApplicational.validateUser(volunteer.getId())

        then: "the volunteer is validated"
        def user = userRepository.findAll().get(1)
        user.getId() == volunteer.getId()
        user.getState().equals(User.State.APPROVED)
        and: "an email is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_2_EMAIL, Mailer.HUMANAETHICA_SUBJECT + userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, _)
    }

    def "the user doesn't exist"() {
        when:
        userServiceApplicational.validateUser(member.getId() + 2)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.AUTHUSER_NOT_FOUND
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