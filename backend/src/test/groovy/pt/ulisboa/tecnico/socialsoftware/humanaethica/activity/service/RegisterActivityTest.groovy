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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class RegisterActivityTest extends SpockTest {
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

    def "the activity already exists"() {
        given:
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = IN_TWO_DAYS
        activityDto.endingDate = IN_THREE_DAYS
        activityDto.applicationDeadline = IN_ONE_DAY
        activityDto.themes = themesDto;
        def themes = new ArrayList()
        themes.add(theme)
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        when:
        activityService.registerActivity(member.getId(), activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_EXISTS
//        activityRepository.findAll().stream().size() == 1
    }

    @Unroll
    def "invalid arguments: name=#name | region=#region"() {
        given: "an activity dto"
        institutionDto = new InstitutionDto(institution)
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)
        activityDto.setParticipantsNumber(1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setInstitution(institutionDto)
        activityDto.setThemes(themesDto)

        when:
        activityService.registerActivity(member.getId(), activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no activity was created"
        activityRepository.count() == 1

        where:
        name           | region          || errorMessage
        null           | ACTIVITY_REGION_1 || ErrorMessage.ACTIVITY_NAME_INVALID
        ""             | ACTIVITY_REGION_1 || ErrorMessage.ACTIVITY_NAME_INVALID
        ACTIVITY_NAME_1 | null || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | ""   || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
  }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}