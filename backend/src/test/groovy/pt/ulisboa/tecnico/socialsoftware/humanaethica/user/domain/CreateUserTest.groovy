package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthDemoUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser

@DataJpaTest
class CreateUserTest extends SpockTest {
    def user

    def "create User: name, username, email, role, state, admin"() {
        when:
        def result = new Volunteer(USER_1_NAME, User.State.SUBMITTED)

        then:
        result.getName() == USER_1_NAME
        result.getRole() == User.Role.VOLUNTEER
    }

    def "create External User: name, username, email, role, state, admin"() {
        when:
        def result = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == User.Role.VOLUNTEER
        result.getAuthUser() != null
        result.getAuthUser().getUsername() == USER_1_USERNAME
        result.getAuthUser().getEmail() == USER_1_EMAIL
        result.getAuthUser() instanceof AuthNormalUser
        !result.getAuthUser().isActive()
    }

    def "create Demo User: name, username, email, role, state, admin"() {
        when:
        def result = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.DEMO, User.State.SUBMITTED)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == User.Role.VOLUNTEER
        result.getAuthUser() != null
        result.getAuthUser().getUsername() == USER_1_USERNAME
        result.getAuthUser().getEmail() == USER_1_EMAIL
        result.getAuthUser() instanceof AuthDemoUser
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}