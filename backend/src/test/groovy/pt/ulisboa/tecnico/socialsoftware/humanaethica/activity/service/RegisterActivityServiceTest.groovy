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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class RegisterActivityServiceTest extends SpockTest {
    def activityDto
    def theme
    def themesDto
    def institution
    def member

    def setup() {
        member = authUserService.loginDemoMemberAuth().getUser()
        institution = institutionService.getDemoInstitution()

        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        themesDto = new ArrayList<>()
    }

    def "register activity"() {
        given: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumber(1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))

        themesDto.add(new ThemeDto(theme,false,false,false))
        activityDto.setThemes(themesDto)

        when:
        def result = activityService.registerActivity(member.getId(), activityDto)

        then: "the returned data is correct"
        result.name == ACTIVITY_NAME_1
        result.region == ACTIVITY_REGION_1
        result.participantsNumber == 1
        result.description == ACTIVITY_DESCRIPTION_1
        result.startingDate == DateHandler.toISOString(IN_TWO_DAYS)
        result.endingDate == DateHandler.toISOString(IN_THREE_DAYS)
        result.applicationDeadline == DateHandler.toISOString(IN_ONE_DAY)
        result.institution.id == institution.id
        result.getThemes().get(0).getName() == THEME_NAME_1
        result.getState() == Activity.State.APPROVED.name()
        and: "the activity is saved in the database"
        activityRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedActivity = activityRepository.findById(result.id).get()
        storedActivity.name == ACTIVITY_NAME_1
        storedActivity.region == ACTIVITY_REGION_1
        storedActivity.participantsNumber == 1
        storedActivity.description == ACTIVITY_DESCRIPTION_1
        storedActivity.startingDate == IN_TWO_DAYS
        storedActivity.endingDate == IN_THREE_DAYS
        storedActivity.applicationDeadline == IN_ONE_DAY
        storedActivity.institution.id == institution.id
    }

    @Unroll
    def 'invalid arguments: name=#name | memberId=#memberId | themeId=#themeId'() {
        given: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumber(1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))

        themesDto.add(getThemeDto(themeId))
        activityDto.setThemes(themesDto)

        when:
        activityService.registerActivity(getId(memberId), activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no activity is stored in the database"
        activityRepository.findAll().size() == 0

        where:
        name            | memberId   | themeId   || errorMessage
        null            | "memberId" | "themeId" || ErrorMessage.ACTIVITY_NAME_INVALID
        ACTIVITY_NAME_1 | null       | "themeId" || ErrorMessage.USER_NOT_FOUND
        ACTIVITY_NAME_1 | "otherId"  | "themeId" || ErrorMessage.USER_NOT_FOUND
        ACTIVITY_NAME_1 | "memberId" | null      || ErrorMessage.THEME_NOT_FOUND
        ACTIVITY_NAME_1 | "memberId" | "otherId" || ErrorMessage.THEME_NOT_FOUND
  }

    def getId(memberId){
        if (memberId == null)
            return null
        else if (memberId == "otherId")
            return 222
        return member.id
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