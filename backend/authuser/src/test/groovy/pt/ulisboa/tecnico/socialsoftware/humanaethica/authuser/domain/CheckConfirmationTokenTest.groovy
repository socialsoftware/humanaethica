package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User

@DataJpaTest
class CheckConfirmationTokenTest extends SpockTest {
    def user
    def authUser

    def setup() {
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.SUBMITTED)
        user = userRepository.save(user)
        authUser = AuthUser.createAuthUser(user.getId(), USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.VOLUNTEER)
        authUserRepository.save(authUser)

    }

    def "checkConfirmationToken: correct token and date has not expired"() {
        given:
        authUser.setConfirmationToken(USER_1_TOKEN)
        authUser.setTokenGenerationDate(NOW)

        when:
        authUser.checkConfirmationToken(USER_1_TOKEN)

        then:
        noExceptionThrown()
    }

    def "checkConfirmationToken: correct token but date has expired"() {
        given:
        authUser.setConfirmationToken(USER_1_TOKEN)
        authUser.setTokenGenerationDate(TWO_DAYS_AGO)

        when:
        authUser.checkConfirmationToken(USER_1_TOKEN)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.EXPIRED_CONFIRMATION_TOKEN
    }

    def "checkConfirmationToken: incorrect token"() {
        given:
        authUser.setConfirmationToken(USER_1_TOKEN)
        authUser.setTokenGenerationDate(NOW)

        when:
        authUser.checkConfirmationToken(USER_2_TOKEN)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INVALID_CONFIRMATION_TOKEN
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
