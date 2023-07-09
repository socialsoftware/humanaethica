package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import spock.lang.Unroll


@DataJpaTest
class RegisterActivityTest extends SpockTest {
    def activityDto
    def institutionDto
    def theme
    def activity
    def institution

    def "the activity does not exist, create the activity"() {
        given: "an activity dto"
        institution = new Institution()
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED,null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,true,true))

        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")
        activityDto.setDescription("ACTIVITY_1_DESCRIPTION")
        activityDto.setStartingDate("2023-05-26T19:09:00Z");
        activityDto.setEndingDate("2023-05-26T22:09:00Z");
        activityDto.setInstitution(new InstitutionDto(institution))
        activityDto.setThemes(themes)

        when:
        def result = activityService.registerActivity(-1, activityDto)

        then: "the activity is saved in the database"
        activityRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getName() == "ACTIVITY_1_NAME"
        result.getRegion() == "ACTIVITY_1_REGION"
        result.getThemes().get(0).getName() == "THEME_1_NAME"
        result.getState() == Activity.State.APPROVED

    }

    def "the activity already exists"() {
        given:
        institution = new Institution()
        institutionRepository.save(institution)
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", "ACTIVITY_1_DESCRIPTION", institution, Activity.State.APPROVED)
        activityRepository.save(activity)
        and:
        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")
        activityDto.setDescription("ACTIVITY_1_DESCRIPTION")
        activityDto.setStartingDate("2023-05-26T19:09:00Z");
        activityDto.setEndingDate("2023-05-26T22:09:00Z");

        when:
        activityService.registerActivity(-1, activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_EXISTS
        and:
        activityRepository.count() == 1
    }

    def "add theme to an activity"() {
        given:
        List<ThemeDto> themes = new ArrayList<>()
        institution = new Institution()
        institutionRepository.save(institution)
        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")
        activityDto.setDescription("ACTIVITY_1_DESCRIPTION")
        activityDto.setStartingDate("2023-05-26T19:09:00Z");
        activityDto.setEndingDate("2023-05-26T22:09:00Z");
        activityDto.setInstitution(new InstitutionDto(institution))
        activityDto.setThemes(themes)
        def result = activityService.registerActivity(-1, activityDto)

        when:
        result.getThemes().size() == 0
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        themeRepository.save(theme)
        themes.add(new ThemeDto(theme, true, true))

        activityDto.setThemes(themes)
        activityService.updateActivity(-1, result.getId(), activityDto)

        then: "the activity is associated with the theme"
        result.getThemes().size() == 1
        result.getThemes().get(0).getName() == "THEME_1_NAME"
    }

    @Unroll
    def "invalid arguments: name=#name | region=#region"() {
        given: "an activity dto"
        institution = new Institution()
        institutionRepository.save(institution)
        institutionDto = new InstitutionDto(institution)
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)
        activityDto.setInstitution(institutionDto)

        when:
        activityService.registerActivity(-1, activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no activity was created"
        activityRepository.count() == 0

        where:
        name               | region               || errorMessage
        null               | "ACTIVITY_1_REGION"  || ErrorMessage.INVALID_ACTIVITY_NAME
        ""                 | "ACTIVITY_1_REGION"  || ErrorMessage.INVALID_ACTIVITY_NAME
        "ACTIVITY_1_NAME"  | null                 || ErrorMessage.INVALID_REGION_NAME
        "ACTIVITY_1_NAME"  | ""                   || ErrorMessage.INVALID_REGION_NAME
  }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}