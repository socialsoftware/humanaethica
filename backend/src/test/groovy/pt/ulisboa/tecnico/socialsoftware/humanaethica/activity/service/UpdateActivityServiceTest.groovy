package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class UpdateActivityServiceTest extends SpockTest {
    def activityDto
    def theme
    def themesDto
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
        and: "a theme"
        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        def themes = new ArrayList<>()
        themes.add(theme)
        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
    }

    def "update activity"() {
        given: 'an activity dto'
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_2)
        activityDto.setRegion(ACTIVITY_REGION_2)
        activityDto.participantsNumber = 2
        activityDto.setDescription(ACTIVITY_DESCRIPTION_2)
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        theme = new Theme(THEME_NAME_2, Theme.State.APPROVED, null)
        themeRepository.save(theme)

        themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme, false, false, false))
        activityDto.setThemes(themesDto)

        when:
        def result = activityService.updateActivity(activity.getId(), activityDto)

        then: "the returned data is correct"
        result.name == ACTIVITY_NAME_2
        result.region == ACTIVITY_REGION_2
        result.participantsNumber == 2
        result.description == ACTIVITY_DESCRIPTION_2
        result.startingDate == DateHandler.toISOString(IN_TWO_DAYS)
        result.endingDate == DateHandler.toISOString(IN_THREE_DAYS)
        result.applicationDeadline == DateHandler.toISOString(IN_ONE_DAY)
        result.getState() == Activity.State.APPROVED.name()
        result.getThemes().size() == 1
        result.getThemes().get(0).getName() == THEME_NAME_2
        and: "the activity is stored"
        activityRepository.findAll().size() == 1
        and: "contains de correct data"
        def storedActivity = activityRepository.findAll().get(0)
        storedActivity.name == ACTIVITY_NAME_2
        storedActivity.region == ACTIVITY_REGION_2
        storedActivity.participantsNumber == 2
        storedActivity.description == ACTIVITY_DESCRIPTION_2
        storedActivity.startingDate == IN_TWO_DAYS
        storedActivity.endingDate == IN_THREE_DAYS
        storedActivity.applicationDeadline == IN_ONE_DAY
        storedActivity.getState() == Activity.State.APPROVED
        storedActivity.getThemes().size() == 1
        storedActivity.getThemes().get(0).getName() == THEME_NAME_2
    }

    @Unroll
    def 'invalid arguments: name=#name | activityId=#activityId | themeId=#themeId'() {
        given: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(ACTIVITY_REGION_2)
        activityDto.participantsNumber = 2
        activityDto.setDescription(ACTIVITY_DESCRIPTION_2)
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        theme = new Theme(THEME_NAME_2, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme, false, false, false))
        activityDto.setThemes(themes)

        themesDto = new ArrayList<>()
        themesDto.add(getThemeDto(themeId))
        activityDto.setThemes(themesDto)

        when:
        activityService.updateActivity(getId(activityId), activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "the activity in in the database"
        activityRepository.findAll().size() == 1

        where:
        name            | activityId   | themeId   || errorMessage
        null            | "activityId" | "themeId" || ErrorMessage.ACTIVITY_NAME_INVALID
        ACTIVITY_NAME_2 | null         | "themeId" || ErrorMessage.ACTIVITY_NOT_FOUND
        ACTIVITY_NAME_2 | "otherId"    | "themeId" || ErrorMessage.ACTIVITY_NOT_FOUND
        ACTIVITY_NAME_2 | "activityId" | null      || ErrorMessage.THEME_NOT_FOUND
        ACTIVITY_NAME_2 | "activityId" | "otherId" || ErrorMessage.THEME_NOT_FOUND
    }

    def getId(activityId){
        if (activityId == null)
            return null
        else if (activityId == "otherId")
            return 222
        return activity.id
    }

    def getThemeDto(themeId) {
        if (themeId == null)
            return new ThemeDto()
        else if (themeId == "otherId") {
            def themeDto = new ThemeDto()
            themeDto.id = 222
            return themeDto
        }
        else
            return new ThemeDto(theme,false,false,false)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}