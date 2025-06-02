package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import spock.lang.Unroll

@DataJpaTest
class GetAuthoritiesTest extends SpockTest {
    def user
    def institutionDto
    def institution
    def authuser


    def setup() {
    }

    @Unroll
    def "get volunteer authorities"() {
        given:
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,  User.State.SUBMITTED)
        user = userRepository.save(user)
        authuser = AuthUser.createAuthUser(user.getId(), user.getUsername(), user.getEmail(), Type.NORMAL, Role.VOLUNTEER, user.getName())
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
        institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)
        institutionDto.setNif(INSTITUTION_1_NIF)

        institution = institutionService.registerInstitution(institutionDto)

        user = new Member(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,  institution, User.State.SUBMITTED)
        user = userRepository.save(user)
        authuser = AuthUser.createAuthUser(user.getId(), user.getUsername(), user.getEmail(), Type.NORMAL, Role.MEMBER, user.getName())
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
        user = new Admin(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.SUBMITTED)
        user = userRepository.save(user)
        authuser = AuthUser.createAuthUser(user.getId(), user.getUsername(), user.getEmail(), Type.NORMAL, Role.ADMIN, user.getName())
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
