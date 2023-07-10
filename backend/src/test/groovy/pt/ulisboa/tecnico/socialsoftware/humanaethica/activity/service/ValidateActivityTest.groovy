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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto


@DataJpaTest
class ValidateActivityTest extends SpockTest {
    public static final String ACTIVITY_1__NAME = "ACTIVITY_1_NAME"
    public static final String ACTIVITY_1__REGION = "ACTIVITY_1_REGION"
    public static final String ACTIVITY_1__DESCRIPTION = "ACTIVITY_1_DESCRIPTION"
    public static final String STARTING_DATE = "2023-05-26T19:09:00Z"
    public static final String ENDING_DATE = "2023-05-26T22:09:00Z"
    public static final String THEME_1__NAME = "THEME_1_NAME"
    def activityDto
    def theme
    def institution
    def activity
    def institutionDto
    def memberDto

    def setup() {
        institution = new Institution()
        institutionRepository.save(institution)
        institutionDto = new InstitutionDto(institution)

        memberDto = new RegisterUserDto()
        memberDto.setEmail(USER_1_EMAIL)
        memberDto.setUsername(USER_1_EMAIL)
        memberDto.setConfirmationToken(USER_1_TOKEN)
        memberDto.setRole(User.Role.MEMBER)
        memberDto.setInstitutionId(institution.getId())

        def member = userService.registerUser(memberDto, null);

        theme = new Theme(THEME_1__NAME, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme, true, true))

        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_1__NAME)
        activityDto.setRegion(ACTIVITY_1__REGION)
        activityDto.setDescription(ACTIVITY_1__DESCRIPTION)
        activityDto.setStartingDate(STARTING_DATE)
        activityDto.setEndingDate(ENDING_DATE)
        activityDto.setInstitution(institutionDto)
        activityDto.setThemes(themes)

        activity = activityService.registerActivity(member.getId(), activityDto)
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
        themes.add(new ThemeDto(theme, true, true))

        activityDto = new ActivityDto(activity, false, false)
        activityDto.setStartingDate(STARTING_DATE)
        activityDto.setEndingDate(ENDING_DATE)
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