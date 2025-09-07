package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.subscriptions

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserDeletedEvent
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser

@DataJpaTest
@Import(AuthUserEventListener)
class OnUserDeletedTest extends SpockTest {

    @Autowired
    AuthUserEventListener listener

    def auth
    def userId = 1

    def setup() {
        auth = AuthUser.createAuthUser(userId, USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.VOLUNTEER)
        auth = authUserRepository.save(auth)
    }

    def "onUserDeleted: directly disables the user"() {
        given:
        def event = new UserDeletedEvent(userId)

        when:
        listener.userDeleted().accept(event)

        then:
        def result = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()
        !result.active
    }
}