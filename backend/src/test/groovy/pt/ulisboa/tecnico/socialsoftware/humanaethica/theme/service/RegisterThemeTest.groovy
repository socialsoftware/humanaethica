package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.RegisterInstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
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
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED)
        //theme.setState(Theme.State.APPROVED)
        themeDto = new ThemeDto(theme)

        when:
        def result = themeService.registerTheme(themeDto)

        then: "the theme is saved in the database"
        themeRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getName() == "THEME_1_NAME"
        result.getState() == Theme.State.APPROVED

    }
    def "the theme already exists"() {
        given:
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED)
        //theme.setState(Theme.State.APPROVED)
        themeRepository.save(theme)
        and:
        themeDto = new ThemeDto()
        themeDto.setName("THEME_1_NAME")
        //themeDto.setState(Theme.State.APPROVED)

        when:
        themeService.registerTheme(themeDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.THEME_ALREADY_EXISTS
        and:
        themeRepository.count() == 1
    }
    def "add institution to a theme"() {
        given:
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED)
        //theme.setState(Theme.State.APPROVED)
        themeDto = new ThemeDto(theme)
        def result = themeService.registerTheme(themeDto)

        when:
        result.getInstitutions().size() == 0
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        List<Institution> institutions = new ArrayList<>()
        institutions.add(institution)
        themeService.addInstitution(result.getId(), institutions)

        then: "the theme is associated with the institution"
        result.getInstitutions().size() == 1
        result.getInstitutions().get(0).getName() == INSTITUTION_1_NAME
        result.getInstitutions().get(0).getEmail() == INSTITUTION_1_EMAIL
        result.getInstitutions().get(0).getNIF() == INSTITUTION_1_NIF
    }

    @Unroll
    /*def "invalid arguments: name=#name"() {
        given: "a theme dto"
        themeDto = new ThemeDto()
        themeDto.setName(name)

        when:
        themeService.registerTheme(themeDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no theme was created"
        themeRepository.count() == 0

        where:
        name               || errorMessage
        null               || ErrorMessage.INVALID_THEME_NAME
        ""                 || ErrorMessage.INVALID_THEME_NAME
    }*/

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}