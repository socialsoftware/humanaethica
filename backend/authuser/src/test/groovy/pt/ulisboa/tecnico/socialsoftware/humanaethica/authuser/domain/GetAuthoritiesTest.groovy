package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import spock.lang.Unroll

@DataJpaTest
class GetAuthoritiesTest extends SpockTest {
    def authuser


    def setup() {
    }

    @Unroll
    def "get volunteer authorities"() {
        given:
        authuser = AuthUser.createAuthUser(1, USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.VOLUNTEER)
        authUserRepository.save(authuser)
        when:
        def result = authuser.getAuthorities()

        then:
        result.size() == 1
        def iter = result.iterator()
        iter.next().getAuthority() == ROLE_VOLUNTEER
    }

    @Unroll
    def "get member authorities"() {
        given:
        authuser = AuthUser.createAuthUser(1, USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.MEMBER)
        authUserRepository.save(authuser)

        when:
        def result = authuser.getAuthorities()

        then:
        result.size() == 1
        def iter = result.iterator()
        iter.next().getAuthority() == ROLE_MEMBER
    }

    @Unroll
    def "get admin authorities"() {
        given:
        authuser = AuthUser.createAuthUser(1, USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.ADMIN)
        authUserRepository.save(authuser)

        when:
        def result = authuser.getAuthorities()

        then:
        result.size() == 1
        def iter = result.iterator()
        iter.next().getAuthority() == ROLE_ADMIN
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
