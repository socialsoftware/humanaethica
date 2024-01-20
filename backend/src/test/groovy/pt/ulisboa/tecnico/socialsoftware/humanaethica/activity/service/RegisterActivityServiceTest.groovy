package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class RegisterActivityServiceTest extends SpockTest {
    def activityDto
    def institutionDto
    def theme
    def themesDto
    def activity
    def institution
    def member

    def setup() {
        member = authUserService.loginDemoMemberAuth().getUser()
        institution = institutionService.getDemoInstitution()

        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme,false,false,false))
    }

    def "the activity does not exist, create the activity"() {
        given: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumber(1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setInstitution(new InstitutionDto(institution))
        activityDto.setThemes(themesDto)

        when:
        def result = activityService.registerActivity(member.getId(), activityDto)

        then: "the activity is saved in the database"
        activityRepository.findAll().size() == 1
        and: "checks if user data is correct"
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

    }

    def "an activity with the same name already exists in the institution"() {
        given:
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        activityDto.setThemes(themesDto)
        def themes = new ArrayList()
        themes.add(theme)
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        when:
        activityService.registerActivity(member.getId(), activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_EXISTS
        and: "only an activity"
        activityRepository.findAll().size() == 1
    }

    def "an activity with the same name already exists in another institution"() {
        given:
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        activityDto.setThemes(themesDto)
        def themes = new ArrayList()
        themes.add(theme)
        def institution2 = new Institution()
        institutionRepository.save(institution2)
        activity = new Activity(activityDto, institution2, themes)
        activityRepository.save(activity)

        when:
        activityService.registerActivity(member.getId(), activityDto)

        then: "two activities"
        activityRepository.findAll().size() == 2
    }

    @Unroll
    def "invalid arguments: name=#name | region=#region | participants=#participants | description=#description | deadline=#deadline | start=#start | end=#end"() {
        given: "an activity dto"
        institutionDto = new InstitutionDto(institution)
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)
        activityDto.setParticipantsNumber(participants)
        activityDto.setDescription(description)
        activityDto.setApplicationDeadline(deadline instanceof LocalDateTime ? DateHandler.toISOString(deadline) : deadline as String)
        activityDto.setStartingDate(start instanceof LocalDateTime ? DateHandler.toISOString(start) : start as String)
        activityDto.setEndingDate(end instanceof LocalDateTime ? DateHandler.toISOString(end) : end as String)
        activityDto.setInstitution(institutionDto)
        activityDto.setThemes(themesDto)

        when:
        activityService.registerActivity(member.getId(), activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no activity was created"
        activityRepository.findAll().size() == 0

        where:
        name            | region            | participants | description            | deadline   | start       | end           || errorMessage
        null            | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_NAME_INVALID
        " "             | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_NAME_INVALID
        ACTIVITY_NAME_1 | null              | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | " "               | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 0            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 6            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | null                   | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | "  "                   | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | null       | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | "  "       | IN_TWO_DAYS | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | null        | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | "   "       | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | null          || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | "     "       || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | NOW         | IN_THREE_DAYS || ErrorMessage.ACTIVITY_APPLICATION_DEADLINE_AFTER_START
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_TWO_DAYS   || ErrorMessage.ACTIVITY_START_AFTER_END
  }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}