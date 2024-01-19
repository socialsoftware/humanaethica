package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution

@DataJpaTest
class ActivityTest extends SpockTest {
    def activityDto
    def institution
    def theme
    def themes
    def activity

    def setup() {
        given: "activity info"
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = IN_TWO_DAYS
        activityDto.endingDate = IN_THREE_DAYS
        activityDto.applicationDeadline = IN_ONE_DAY
        and: "an institution"
        institution = new Institution()
        institutionRepository.save(institution)
        and: "a theme"
        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        themes = new ArrayList<>()
        themes.add(theme)
    }

    def "create empty activity"() {
        when:
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        then: "checks if activity is saved"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getId() != 0
        result.getThemes().get(0).getId() == theme.getId()

        and: "the theme has a reference for the activity"
        theme.getActivities().size() == 1
        theme.getActivities().contains(result)
    }

    def "create activity and persists"() {
        when: "activity is created"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        then: "activity is saved"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)

        and: "checks if activity data is correct"
        result.getId() != 0
        result.getName() == ACTIVITY_NAME_1
        result.getRegion() == ACTIVITY_REGION_1
        result.getDescription() == ACTIVITY_DESCRIPTION_1
        result.getThemes().get(0).getName() == THEME_NAME_1

        and: "the theme has a reference for the activity"
        theme.getActivities().size() == 1
        theme.getActivities().contains(result)
    }

    def "remove a theme from activity"() {
        given: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        when: "the theme is removed"
        activity.removeTheme(theme)

        then: "the theme is no longer persisted in the activity"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getThemes().size() == 0
    }

    def "suspend activity"() {
        given: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        when: "the activity is suspended"
        activity.suspend()

        then: "the activity is no longer persisted"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getState() == Activity.State.SUSPENDED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}