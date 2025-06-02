package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role

@DataJpaTest
class UserAuthTest extends SpockTest {

    User user
    AuthUser authUser


    def setup() {
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.SUBMITTED)
        user = userRepository.save(user)
        authUser = AuthUser.createAuthUser(user.getId(), USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.VOLUNTEER, USER_1_NAME)
        authUser.setActive(true)
        authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        authUserRepository.save(authUser)
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
