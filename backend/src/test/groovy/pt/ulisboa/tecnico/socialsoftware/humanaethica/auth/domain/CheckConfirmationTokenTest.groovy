package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class CheckConfirmationTokenTest extends SpockTest {
    def user

    def setup() {
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(user)
    }

    def "checkConfirmationToken: correct token and date has not expired"() {
        given:
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(NOW)

        when:
        user.getAuthUser().checkConfirmationToken(USER_1_TOKEN)

        then:
        noExceptionThrown()
    }

    def "checkConfirmationToken: correct token but date has expired"() {
        given:
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(TWO_DAYS_AGO)

        when:
        user.getAuthUser().checkConfirmationToken(USER_1_TOKEN)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.EXPIRED_CONFIRMATION_TOKEN
    }

    def "checkConfirmationToken: incorrect token"() {
        given:
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(NOW)

        when:
        user.getAuthUser().checkConfirmationToken(USER_2_TOKEN)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INVALID_CONFIRMATION_TOKEN
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
