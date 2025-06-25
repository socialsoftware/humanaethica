package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.events

import com.google.common.eventbus.EventBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserDeletedEvent
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user.UserRegisteredEvent
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import spock.mock.DetachedMockFactory


@DataJpaTest
class EventTest extends SpockTest{


    @Autowired
    EventBus busMock

    def dto

    def setup() {
        dto = new RegisterUserDto()
        dto.setUsername(USER_1_USERNAME)
        dto.setEmail(USER_1_EMAIL)
        dto.setRole(Role.VOLUNTEER)
        dto.setName(USER_1_NAME)
    }

    def "handleUserRegistrationEvent: is correctly triggered by event bus"() {
        given:

        when:
        userService.registerUser(dto, null)

        then:
        def result = userRepository.findUserByUsername(USER_1_USERNAME).get()
        result.username == USER_1_USERNAME
        result.email == USER_1_EMAIL
        result.role.name() == Role.VOLUNTEER.name()
        result.id != null
        and:
        1 * busMock.post(_ as UserRegisteredEvent) >> { UserRegisteredEvent event ->
            assert event.getRegisterUserDto().getUsername() == USER_1_USERNAME
            assert event.getRegisterUserDto().getEmail() == USER_1_EMAIL
            assert event.getRegisterUserDto().getRole() == Role.VOLUNTEER
            assert event.getType() == Type.NORMAL
        }
    }

    def "onUserDeleted: disables user triggering by event bus"() {
        given: "register listener"
        def user = userService.registerUser(dto, null)

        when:
        userService.deleteUser(user.getId())

        then:
        def result = userRepository.findUserByUsername(USER_1_USERNAME).get()
        result.state.name() == "DELETED"
        and:
        1 * busMock.post(_ as UserDeletedEvent) >> { UserDeletedEvent event ->
            assert event.getUserId() == user.getId()
        }

    }





    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {
        def mockFactory = new DetachedMockFactory()

        @Bean
        EventBus eventBus() {
            return mockFactory.Mock(EventBus)
        }
    }


}
