package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import spock.lang.Unroll

@DataJpaTest
class UpdateActivityTest extends SpockTest {
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

    def "add theme to an activity"() {
        given: 'an activity'
        activity = new Activity(ACTIVITY_1__NAME, ACTIVITY_1__REGION, ACTIVITY_1__DESCRIPTION, institution, STARTING_DATE, ENDING_DATE, Activity.State.APPROVED)
        activityRepository.save(activity)
        and:
        theme = new Theme(THEME_1__NAME, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        and:
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_1__NAME)
        activityDto.setRegion(ACTIVITY_1__REGION)
        activityDto.setDescription(ACTIVITY_1__DESCRIPTION)
        activityDto.setStartingDate(STARTING_DATE);
        activityDto.setEndingDate(ENDING_DATE);
        activityDto.setInstitution(new InstitutionDto(institution))
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme, true, true))
        activityDto.setThemes(themes)

        when:
        activityService.updateActivity(member.getId(), activity.getId(), activityDto)

        then: "the activity is associated with the theme"
        activityRepository.findAll().size() == 1
        def result = activityRepository.findAll().get(0)
        result.getThemes().size() == 1
        result.getThemes().get(0).getName() == THEME_1__NAME
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}