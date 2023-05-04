package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.RegisterThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.RegisterInstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@DataJpaTest
class RegisterThemeTest extends SpockTest {
    def theme
    def themeDto
    def institution
    def institutionDto
    def institutionDto2

    def "the theme does not exist, create the theme"() {
        given: "a theme dto"
        themeDto = new RegisterThemeDto()
        themeDto.setName("THEME_1_NAME")

        when:
        def result = themeService.registerTheme(themeDto)

        then: "the theme is saved in the database"
        themeRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getName() == "THEME_1_NAME"

    }
    def "associates a theme to an institution"() {
        given:
        themeDto = new RegisterThemeDto()
        themeDto.setName("THEME_1_NAME")
        institutionDto = new RegisterInstitutionDto()
        institutionDto.setInstitutionEmail(INSTITUTION_1_EMAIL)
        institutionDto.setInstitutionName(INSTITUTION_1_NAME)
        institutionDto.setInstitutionNif(INSTITUTION_1_EMAIL)

        when:
        InstitutionDto institutionDto2 = new InstitutionDto(institutionDto);
        def result = institutionService.registerInstitution(institutionDto2)
        def result1 = themeService.registerTheme(themeDto)

        then: "the theme and institution are saved in the database"
        themeRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result1.getName() == "THEME_1_NAME"
        result1.getInstitutions().get(0).getName() == institutionDto.getName()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}