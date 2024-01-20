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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@DataJpaTest
class ValidateActivityServiceTest extends SpockTest {
    def activityDto
    def activityResultDto
    def theme
    def institution
    def institutionDto
    def memberDto
    def member

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

        member = userService.registerUser(memberDto, null);

        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme, false, false, false))

        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumber(2)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(NOW))
        activityDto.setInstitution(institutionDto)
        activityDto.setThemes(themes)

        activityResultDto = activityService.registerActivity(member.getId(), activityDto)
        activityService.reportActivity(activityResultDto.getId())
    }

    def "validate activity with success"() {
        when:
        activityService.validateActivity(activityResultDto.getId())

        then: "the activity and theme are validated"
        activityResultDto.getState() == Activity.State.APPROVED.name()
   }

    def "the activity doesn't exist"() {
        when:
        activityService.validateActivity(activityResultDto.getId() + 1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def "the activity is already approved"() {
        when:
        activityService.validateActivity(activityResultDto.getId())
        activityService.validateActivity(activityResultDto.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_APPROVED
    }

    def "the theme haven't yet been approved"() {
        given:
        theme.setState(Theme.State.SUBMITTED)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme, false, false, false))

        activityResultDto.setStartingDate(DateHandler.toISOString(IN_ONE_DAY))
        activityResultDto.setEndingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityResultDto.setThemes(themes)

        when:
        activityService.updateActivity(activityResultDto.getId(), activityDto)
        activityService.validateActivity(c.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.THEME_NOT_APPROVED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}