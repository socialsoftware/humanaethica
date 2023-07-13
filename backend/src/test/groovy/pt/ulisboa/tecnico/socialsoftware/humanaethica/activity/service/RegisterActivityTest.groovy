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
import spock.lang.Unroll


@DataJpaTest
class RegisterActivityTest extends SpockTest {
    public static final String ACTIVITY_1__NAME = "ACTIVITY_1_NAME"
    public static final String ACTIVITY_1__REGION = "ACTIVITY_1_REGION"
    public static final String ACTIVITY_1__DESCRIPTION = "ACTIVITY_1_DESCRIPTION"
    public static final String STARTING_DATE = "2023-05-26T19:09:00Z"
    public static final String ENDING_DATE = "2023-05-26T22:09:00Z"
    public static final String THEME_1__NAME = "THEME_1_NAME"

    def activityDto
    def institutionDto
    def theme
    def activity
    def institution
    def member

    def setup() {
        member = authUserService.loginDemoMemberAuth().getUser()
        institution = institutionService.getDemoInstitution()
    }

    def "the activity does not exist, create the activity"() {
        given: "an activity dto"
        theme = new Theme(THEME_1__NAME, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,true,true))

        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_1__NAME)
        activityDto.setRegion(ACTIVITY_1__REGION)
        activityDto.setDescription(ACTIVITY_1__DESCRIPTION)
        activityDto.setStartingDate(STARTING_DATE);
        activityDto.setEndingDate(ENDING_DATE);
        activityDto.setInstitution(new InstitutionDto(institution))
        activityDto.setThemes(themes)

        when:
        def result = activityService.registerActivity(member.getId(), activityDto)

        then: "the activity is saved in the database"
        activityRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getName() == ACTIVITY_1__NAME
        result.getRegion() == ACTIVITY_1__REGION
        result.getThemes().get(0).getName() == THEME_1__NAME
        result.getState() == Activity.State.APPROVED

    }

    def "the activity already exists"() {
        given:
        activity = new Activity(ACTIVITY_1__NAME, ACTIVITY_1__REGION, ACTIVITY_1__DESCRIPTION, institution, STARTING_DATE, ENDING_DATE, Activity.State.APPROVED)
        activityRepository.save(activity)
        and:
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_1__NAME)
        activityDto.setRegion(ACTIVITY_1__REGION)
        activityDto.setDescription(ACTIVITY_1__DESCRIPTION)
        activityDto.setStartingDate(STARTING_DATE);
        activityDto.setEndingDate(ENDING_DATE);

        when:
        activityService.registerActivity(member.getId(), activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_EXISTS
        activityRepository.count() == 1
    }

    @Unroll
    def "invalid arguments: name=#name | region=#region"() {
        given: "an activity dto"
        institutionDto = new InstitutionDto(institution)
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)
        activityDto.setInstitution(institutionDto)

        when:
        activityService.registerActivity(member.getId(), activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no activity was created"
        activityRepository.count() == 0

        where:
        name               | region               || errorMessage
        null               | ACTIVITY_1__REGION || ErrorMessage.INVALID_ACTIVITY_NAME
        ""                 | ACTIVITY_1__REGION || ErrorMessage.INVALID_ACTIVITY_NAME
        ACTIVITY_1__NAME  | null                 || ErrorMessage.INVALID_REGION_NAME
        ACTIVITY_1__NAME  | ""                   || ErrorMessage.INVALID_REGION_NAME
  }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}