package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import spock.lang.Unroll

@DataJpaTest
class GetAuthoritiesTest extends SpockTest {
    def user

    def setup() {
    }

    @Unroll
    def "get volunteer authorities"() {
        given:
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL)
        userRepository.save(user)

        when:
        def result = user.getAuthUser().getAuthorities()

        then:
        result.size() == 1
        def iter = result.iterator()
        iter.next().getAuthority() == ROLE_VOLUNTEER
    }

    @Unroll
    def "get member authorities"() {
        given:
        user = new Member(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL)
        userRepository.save(user)

        when:
        def result = user.getAuthUser().getAuthorities()

        then:
        result.size() == 1
        def iter = result.iterator()
        iter.next().getAuthority() == ROLE_MEMBER
    }

    @Unroll
    def "get admin authorities"() {
        given:
        user = new Admin(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL)
        userRepository.save(user)

        when:
        def result = user.getAuthUser().getAuthorities()

        then:
        result.size() == 1
        def iter = result.iterator()
        iter.next().getAuthority() == ROLE_ADMIN
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
