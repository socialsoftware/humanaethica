package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class DemoAuthTest extends SpockTest {

    def "demo member login"() {
        when:
        def result = authUserService.loginDemoMemberAuth();

        then:
        result.user.name == DemoUtils.DEMO_MEMBER
        result.user.role == User.Role.MEMBER
    }

    def "demo volunteer login"() {
        when:
        def result = authUserService.loginDemoVolunteerAuth();

        then:
        result.user.name == DemoUtils.DEMO_VOLUNTEER
        result.user.role == User.Role.VOLUNTEER
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
