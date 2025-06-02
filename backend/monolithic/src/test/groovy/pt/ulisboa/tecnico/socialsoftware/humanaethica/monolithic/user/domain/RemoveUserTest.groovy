package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer

@DataJpaTest
class RemoveUserTest extends SpockTest {
    def user

    def setup() {
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.SUBMITTED)
        userRepository.save(user)
    }

    def "remove normal active user"() {
        given:

        when:
        userService.deleteUser(user.getId())

        then:
        user.getState() == User.State.DELETED
        and:
        userRepository.findAll().size() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
