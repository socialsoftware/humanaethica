package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import spock.lang.Unroll

@DataJpaTest
class RegisterThemeTest extends SpockTest {
    def theme
    def themeDto
    def institution

    def "the theme does not exist, create the theme"() {
        given: "a theme dto"
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED,null)
        themeDto = new ThemeDto(theme,true,false,false)

        when:
        def result = themeService.registerTheme(themeDto,true)

        then: "the theme is saved in the database"
        themeRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getName() == "THEME_1_NAME"
        result.getState() == Theme.State.APPROVED

    }

    def "the theme already exists"() {
        given:
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED,null)
        themeRepository.save(theme)
        and:
        themeDto = new ThemeDto()
        themeDto.setName("THEME_1_NAME")

        when:
        themeService.registerTheme(themeDto,true)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.THEME_ALREADY_EXISTS
        and:
        themeRepository.count() == 1
    }
    def "add institution to a theme"() {
        given:
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED,null)
        themeDto = new ThemeDto(theme,true,false, true)
        def result = themeService.registerTheme(themeDto,true)

        when:
        result.getInstitutions().size() == 0
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        themeService.addInstitutionToTeam(result.getId(), institution.getId());

        then: "the theme is associated with the institution"
        result.getInstitutions().size() == 1
        result.getInstitutions().get(0).getName() == INSTITUTION_1_NAME
        result.getInstitutions().get(0).getEmail() == INSTITUTION_1_EMAIL
        result.getInstitutions().get(0).getNIF() == INSTITUTION_1_NIF
    }

    def "getThemes from institution"() {
        given:
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED,null)
        //theme.setState(Theme.State.APPROVED)
        themeDto = new ThemeDto(theme,false,false, false)
        def result = themeService.registerTheme(themeDto,true)
        result.getInstitutions().size() == 0
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        themeService.addInstitutionToTeam(result.getId(), institution.getId());

        when:
        List<ThemeDto> aux = themeService.getInstitutionThemes( institution.getId());
        List<ThemeDto> aux_2 = themeService.getThemes();

        then: "the theme is associated with the institution"
        aux.size() == 1;
        aux.get(0).getName().equals("THEME_1_NAME");
        aux_2.size() == 1;
    }



    def "create and delete four themes associated with each other"(){
        given:
        def themeParent = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        themeRepository.save(themeParent)
        def subTheme1 = new Theme("THEME_2_NAME", Theme.State.APPROVED,themeParent)
        themeRepository.save(subTheme1)
        def subTheme2 = new Theme("THEME_3_NAME", Theme.State.APPROVED,themeParent)
        themeRepository.save(subTheme2)
        def subTheme3 = new Theme("THEME_2_NAME", Theme.State.APPROVED,subTheme1)
        themeRepository.save(subTheme3)
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        themeService.addInstitutionToTeam(subTheme3.getId(), institution.getId());

        when:
        themeService.deleteTheme(themeParent.getId());

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.THEME_CAN_NOT_BE_DELETED
        themeParent.isActive()
        subTheme1.isActive()
        subTheme2.isActive()
        subTheme3.isActive()
    }

    @Unroll
    def "invalid arguments: name=#name"() {
        given: "a theme dto"
        themeDto = new ThemeDto()
        themeDto.setName(name)

        when:
        themeService.registerTheme(themeDto,true)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no theme was created"
        themeRepository.count() == 0

        where:
        name               || errorMessage
        null               || ErrorMessage.INVALID_THEME_NAME
        ""                 || ErrorMessage.INVALID_THEME_NAME
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}