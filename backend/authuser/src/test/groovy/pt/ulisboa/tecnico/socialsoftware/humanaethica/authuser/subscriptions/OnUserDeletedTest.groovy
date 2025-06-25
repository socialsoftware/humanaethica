package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.subscriptions

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserDeletedEvent
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.dto.UserDto


@DataJpaTest
class OnUserDeletedTest extends SpockTest{


    @Autowired
    AuthUserEventListener listener
    def auth
    def userId = 1

    def setup() {

        auth = AuthUser.createAuthUser(userId, USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.VOLUNTEER)
        auth = authUserRepository.save(auth)

    }

    def "onUserDeleted: directly disables the user"() {
        given: "an event for existing user"
        def event = new UserDeletedEvent(userId)

        when:
        listener.onUserDeleted(event)

        then:
        def result = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()
        !result.active
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}
