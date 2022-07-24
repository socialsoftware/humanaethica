package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

@DataJpaTest
class UserAuthTest extends SpockTest {

    User user
    AuthUser authUser


    def setup() {
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        user.getAuthUser().setActive(true)
        userRepository.save(user)
        user.getAuthUser().setPassword(passwordEncoder.encode(USER_1_PASSWORD))
    }

    def "user logins successfully"() {
        when:
        def result = authUserService.loginUserAuth(USER_1_USERNAME, USER_1_PASSWORD)

        then:
        result.user.username == USER_1_USERNAME
    }

    def "login fails, given values are invalid"() {
        when:
        authUserService.loginUserAuth(username, password)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        username        | password        || errorMessage
        null            | USER_1_PASSWORD || ErrorMessage.USER_NOT_FOUND
        USER_2_USERNAME | USER_2_PASSWORD || ErrorMessage.USER_NOT_FOUND
        USER_1_USERNAME | USER_2_PASSWORD || ErrorMessage.INVALID_PASSWORD
        USER_1_USERNAME | null            || ErrorMessage.INVALID_PASSWORD
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
