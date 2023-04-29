package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest


import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository


@DataJpaTest
class CreateandRemoveTheme extends SpockTest {

    def "create Theme: name"() {
        when:
        def theme = new Theme("Animals")
        //ThemeRepository.save(theme)

        then:
        theme.getName() == "Animals"
        theme.isActive()
        //ThemeRepository.count() == 1L
    }

    def "crete and delete Theme"(){
        given:
        def theme = new Theme("Animals")
        //ThemeRepository.save(theme)

        when:
        theme.delete()

        then:
        !theme.isActive()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}