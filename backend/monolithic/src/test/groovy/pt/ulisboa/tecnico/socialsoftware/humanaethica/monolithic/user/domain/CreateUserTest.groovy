package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role

@DataJpaTest
class CreateUserTest extends SpockTest {
    def user

    def "create User: name, username, email, role, state, admin"() {
        when:
        def result = new Volunteer(USER_1_NAME, State.SUBMITTED)

        then:
        result.getName() == USER_1_NAME
        result.getRole() == Role.VOLUNTEER
    }

    def "create External User: name, username, email, role, state, admin"() {
        when:
        def result = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,  State.SUBMITTED)

        then:
        result.getName() == USER_1_NAME
        result.getUsername() == USER_1_USERNAME
        result.getRole() == Role.VOLUNTEER
        result.getEmail() == USER_1_EMAIL
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}