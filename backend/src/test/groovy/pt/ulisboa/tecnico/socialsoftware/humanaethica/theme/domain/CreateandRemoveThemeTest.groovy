package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto

@DataJpaTest
class CreateandRemoveThemeTest extends SpockTest {

    def "create Theme: name"() {
        when:
        def theme = new Theme("THEME_1_NAME", Theme.State.APPROVED,null)
        themeRepository.save(theme)

        then:
        themeRepository.count() == 1L
        def result = themeRepository.findAll().get(0)
        result.getName() == "THEME_1_NAME"
        result.isActive()

    }

    def "delete Theme"(){
        given:
        def theme = new Theme("THEME_1_NAME",Theme.State.APPROVED, null)
        themeRepository.save(theme)

        when:
        theme.delete()

        then:
        !theme.isActive()
    }

    def "create two themes associated with each other"(){
        when:
        def themeParent = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        def subTheme = new Theme("THEME_2_NAME", Theme.State.APPROVED,themeParent)

        then:
        themeParent.getSubThemes().size() == 1
        themeParent.getSubThemes().get(0).getName() =="THEME_2_NAME"
        subTheme.getParentTheme().getName() == "THEME_1_NAME"

    }

    def "create and delete two themes associated with each other"(){
        given:
        def themeParent = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        themeRepository.save(themeParent)
        def subTheme = new Theme("THEME_2_NAME", Theme.State.APPROVED,themeParent)
        themeRepository.save(subTheme)

        when:
        themeService.deleteTheme(themeParent.getId());

        then:
        !themeParent.isActive()
        !subTheme.isActive()
    }


    def "create and delete four themes associated with each other"(){
        given:
        def themeParent = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        themeRepository.save(themeParent)
        def subTheme1 = new Theme("THEME_2_NAME", Theme.State.APPROVED,themeParent)
        themeRepository.save(subTheme1)
        def subTheme2 = new Theme("THEME_3_NAME", Theme.State.APPROVED,themeParent)
        themeRepository.save(subTheme2)
        def subTheme3 = new Theme("THEME_2_NAME", Theme.State.APPROVED,subTheme1)
        themeRepository.save(subTheme3)

        when:
        themeService.deleteTheme(themeParent.getId());

        then:
        !themeParent.isActive()
        !subTheme1.isActive()
        !subTheme2.isActive()
        !subTheme3.isActive()
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}