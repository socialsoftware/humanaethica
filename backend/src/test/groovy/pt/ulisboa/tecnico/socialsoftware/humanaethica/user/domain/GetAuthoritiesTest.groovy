package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import spock.lang.Unroll

@DataJpaTest
class GetAuthoritiesTest extends SpockTest {
    def user
    def institutionDto
    def institution

    def setup() {
    }

    @Unroll
    def "get volunteer authorities"() {
        given:
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
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
        institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)
        institutionDto.setNif(INSTITUTION_1_NIF)

        institution = institutionService.registerInstitution(institutionDto)

        user = new Member(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, institution, User.State.SUBMITTED)
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
        user = new Admin(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
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
