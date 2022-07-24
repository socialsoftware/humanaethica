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
import spock.lang.Unroll


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
        institutionDto.setNif(INSTITUTION_1_NIF)

        when:
        def result = institutionService.registerInstitution(institutionDto)

        then: "the institution is saved in the database"
        institutionRepository.findAll().size() == 1
        def institution = institutionRepository.findAll().get(0)
        institution.getConfirmationToken() != null
        and: "checks if institution data is correct"
        result.getName() == INSTITUTION_1_NAME
        result.getEmail() == INSTITUTION_1_EMAIL
        !result.isActive()
    }

    def "this institution already exists"() {
        given:
        def institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        institution.validate()
        and:
        institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)
        institutionDto.setNif(INSTITUTION_1_NIF)

        when:
        institutionService.registerInstitution(institutionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.NIF_ALREADY_EXIST
        and:
        institutionRepository.count() == 1
    }

    @Unroll
    def "invalid arguments: name=#name | email=#email | nif=#nif"() {
        given: "an institution dto"
        institutionDto = new InstitutionDto()
        institutionDto.setName(name)
        institutionDto.setEmail(email)
        institutionDto.setNif(nif)

        when:
        institutionService.registerInstitution(institutionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no institution was created"
        institutionRepository.count() == 0

        where:
        name                | email                  | nif                || errorMessage
        null                | INSTITUTION_1_EMAIL    | INSTITUTION_1_NIF  || ErrorMessage.INVALID_INSTITUTION_NAME
        "  "                | INSTITUTION_1_EMAIL    | INSTITUTION_1_NIF  || ErrorMessage.INVALID_INSTITUTION_NAME
        INSTITUTION_1_NAME  | null                   | INSTITUTION_1_NIF  || ErrorMessage.INVALID_EMAIL
        INSTITUTION_1_NAME  | ""                     | INSTITUTION_1_NIF  || ErrorMessage.INVALID_EMAIL
        INSTITUTION_1_NAME  | "test.mail.com"        | INSTITUTION_1_NIF  || ErrorMessage.INVALID_EMAIL
        INSTITUTION_1_NAME  | "test@"                | INSTITUTION_1_NIF  || ErrorMessage.INVALID_EMAIL
        INSTITUTION_1_NAME  | INSTITUTION_1_EMAIL    | null               || ErrorMessage.INVALID_NIF
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