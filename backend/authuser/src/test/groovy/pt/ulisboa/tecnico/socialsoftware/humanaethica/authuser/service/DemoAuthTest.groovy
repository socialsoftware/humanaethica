package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role

@DataJpaTest
class DemoAuthTest extends SpockTest {

    def "demo member login"() {
        when:
        def result = authUserService.loginDemoMemberAuth()

        then:
        result.user.username == DemoUtils.DEMO_MEMBER.toLowerCase()
        result.user.role == Role.MEMBER
    }

    def "demo volunteer login"() {
        when:
        def result = authUserService.loginDemoVolunteerAuth()

        then:
        result.user.username == DemoUtils.DEMO_VOLUNTEER.toLowerCase()
        result.user.role == Role.VOLUNTEER
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
