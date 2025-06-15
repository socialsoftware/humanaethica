package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthDemoUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import spock.lang.Unroll

@DataJpaTest
class CreateAuthUserTest extends SpockTest {
    def user
    def authUser
    def userId

    def setup() {
        user = new Volunteer(USER_1_NAME, User.State.SUBMITTED)
        user = userRepository.save(user)
        userId = user.getId()
    }

    @Unroll
    def "create auth user type=#type | active=#active | type=#type"() {
        given:
        authUser = AuthUser.createAuthUser(userId, USER_1_USERNAME, USER_1_EMAIL, type, Role.VOLUNTEER)
        authUserRepository.save(authUser)

        when:
        def objType = createClassAutUser(type).getClass()

        then:
        def authUser = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()
        authUser.username == USER_1_USERNAME
        authUser.email == USER_1_EMAIL
        authUser.isActive().equals(active)
        and: "the user and authUser are connected"
        authUser.userId != null
        authUser.userId == userId

        objType.isInstance(authUser)

        where:
        type                 | active
        Type.NORMAL | false
        Type.DEMO   | true
    }

    def createClassAutUser(Type type) {
        switch (type) {
            case Type.NORMAL: return new AuthNormalUser()
            case Type.DEMO: return new AuthDemoUser()
        }
    }

    /*@TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}*/
}
