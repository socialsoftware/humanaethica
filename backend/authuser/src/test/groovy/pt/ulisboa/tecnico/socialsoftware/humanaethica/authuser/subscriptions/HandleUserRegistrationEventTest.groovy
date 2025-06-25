package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.subscriptions

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserRegisteredEvent
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role

@DataJpaTest
class HandleUserRegistrationEventTest extends SpockTest {


    @Autowired
    AuthUserEventListener listener
    def dto
    def event

    def setup() {
        dto = new RegisterUserDto()
        dto.setUsername(USER_1_USERNAME)
        dto.setEmail(USER_1_EMAIL)
        dto.setRole(Role.VOLUNTEER)
        dto.setName(USER_1_NAME)
        event = new UserRegisteredEvent(dto,1, Type.NORMAL, dto.getRole(), dto.getName())
    }

    def "directly creates auth user successfully"() {
        given: "a valid registration event"


        when: "the listener handles the event directly"
        listener.hanldeUserRegistationEvent(event)

        then: "the user is persisted"
        authUserRepository.count() == 1
        def result = authUserRepository.findAuthUserByUsername(USER_1_USERNAME).get()
        result.username == USER_1_USERNAME
        result.email == USER_1_EMAIL
        result.role.name() == Role.VOLUNTEER.name()
        result.userId != null
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}
