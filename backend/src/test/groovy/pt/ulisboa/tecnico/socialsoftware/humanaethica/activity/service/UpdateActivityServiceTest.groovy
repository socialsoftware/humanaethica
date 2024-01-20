package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@DataJpaTest
class UpdateActivityServiceTest extends SpockTest {
    def activityDto
    def theme
    def activity
    def institution
    def member

    def setup() {
        member = authUserService.loginDemoMemberAuth().getUser()
        institution = institutionService.getDemoInstitution()
        given: "activity info"
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 1
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        and: "an institution"
        institution = new Institution()
        institutionRepository.save(institution)
        and: "a theme"
        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        def themes = new ArrayList<>()
        themes.add(theme)
        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
    }

    def "add theme to an activity"() {
        given: 'an activity dto'
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_2)
        activityDto.setRegion(ACTIVITY_REGION_2)
        activityDto.participantsNumber = 2
        activityDto.setDescription(ACTIVITY_DESCRIPTION_2)
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        activityDto.setInstitution(new InstitutionDto(institution))
        theme = new Theme(THEME_NAME_2, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme, false, false, false))
        activityDto.setThemes(themes)

        when:
        activityService.updateActivity(activity.getId(), activityDto)

        then: "the activity is associated with the theme"
        activityRepository.findAll().size() == 1
        def result = activityRepository.findAll().get(0)
        result.name == ACTIVITY_NAME_2
        result.region == ACTIVITY_REGION_2
        result.participantsNumber == 2
        result.description == ACTIVITY_DESCRIPTION_2
        result.startingDate == IN_TWO_DAYS
        result.endingDate == IN_THREE_DAYS
        result.applicationDeadline == IN_ONE_DAY
        result.institution.id == institution.id
        result.getState() == Activity.State.APPROVED
        result.getThemes().size() == 1
        result.getThemes().get(0).getName() == THEME_NAME_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}