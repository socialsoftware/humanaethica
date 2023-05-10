package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class CreateandRemoveThemeTest extends SpockTest {

    def "create Theme: name"() {
        when:
        def theme = new Theme("THEME_1_NAME", Theme.State.APPROVED)
        themeRepository.save(theme)

        then:
        themeRepository.count() == 1L
        def result = themeRepository.findAll().get(0)
        result.getName() == "THEME_1_NAME"
        result.isActive()

    }

    def "delete Theme"(){
        given:
        def theme = new Theme("THEME_1_NAME",Theme.State.APPROVED)
        themeRepository.save(theme)

        when:
        theme.delete()

        then:
        !theme.isActive()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}