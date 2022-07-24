package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import spock.lang.Unroll

@DataJpaTest
class CreateAuthUserTest extends SpockTest {
    def user
    def authUser

    def setup() {
        user = new Volunteer(USER_1_NAME, User.State.SUBMITTED)
        userRepository.save(user)
    }

    @Unroll
    def "create auth user type=#type | active=#active | type=#type"() {
        given:
        authUser = AuthUser.createAuthUser(user, USER_1_USERNAME, USER_1_EMAIL, type)
        authUserRepository.save(authUser)

        when:
        def objType = createClassAutUser(type).getClass()

        then:
        def authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()
        authUser.username == USER_1_USERNAME
        authUser.email == USER_1_EMAIL
        authUser.isActive().equals(active)
        and: "the user and authUser are connected"
        authUser.user != null
        authUser.user.equals(user)

        objType.isInstance(authUser)

        where:
        type                 | active
        AuthUser.Type.NORMAL | false
        AuthUser.Type.DEMO   | true
    }

    def createClassAutUser(AuthUser.Type type) {
        switch (type) {
            case AuthUser.Type.NORMAL: return new AuthNormalUser()
            case AuthUser.Type.DEMO: return new AuthDemoUser()
        }
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
