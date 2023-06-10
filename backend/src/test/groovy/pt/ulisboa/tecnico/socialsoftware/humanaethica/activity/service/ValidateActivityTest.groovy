package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration

import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto


@DataJpaTest
class ValidateActivityTest extends SpockTest {
    def activityDto
    def theme
    def institution
    def activity
    def institutionDto

    def setup() {
        institution = new Institution()
        institutionRepository.save(institution)
        institutionDto = new InstitutionDto(institution)
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme))

        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")
        activityDto.setDescription("ACTIVITY_1_DESCRIPTION")
        activityDto.setStartingDate("2023-05-26T19:09:00Z")
        activityDto.setEndingDate("2023-05-26T22:09:00Z")
        activityDto.setInstitution(institutionDto)
        activityDto.setThemes(themes)

        activity = activityService.registerActivity(-1, activityDto)
        activityService.reportActivity(activity.getId())
    }

    def "validate activity with success"() {
        when:
        activityService.validateActivity(activity.getId())

        then: "the activity and theme are validated"
        activity.getState() == Activity.State.APPROVED
   }

    def "the activity doesn't exist"() {
        when:
        activityService.validateActivity(activity.getId() + 1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def "the activity is already approved"() {
        when:
        activityService.validateActivity(activity.getId())
        activityService.validateActivity(activity.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_APPROVED
    }

    def "the theme haven't yet been approved"() {
        given:
        theme.setState(Theme.State.SUBMITTED)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme))

        activityDto = new ActivityDto(activity, false, false)
        activityDto.setStartingDate("2023-05-26T19:09:00Z")
        activityDto.setEndingDate("2023-05-26T22:09:00Z")
        activityDto.setThemes(themes)

        when:
        activityService.updateActivity(-1, activity.getId(), activityDto)
        activityService.validateActivity(activity.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.THEME_NOT_APPROVED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}