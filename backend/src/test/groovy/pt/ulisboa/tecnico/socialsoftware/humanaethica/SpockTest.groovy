package pt.ulisboa.tecnico.socialsoftware.humanaethica

import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.ActivitySuggestionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.repository.ActivitySuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthPasswordDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.repository.AuthUserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.EnrollmentRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.EnrollmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.InstitutionProfileService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.repository.InstitutionProfileRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.repository.VolunteerProfileRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.VolunteerProfileService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.ReportRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.ReportService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain.Report
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserApplicationalService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.ActivityService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.repository.ThemeRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.ThemeService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import spock.lang.Specification

import java.time.LocalDateTime

class SpockTest extends Specification {
    // remote requests

    WebClient webClient
    HttpHeaders headers

    // send email mocking

    @Value('${spring.mail.username}')
    public String mailerUsername

    @Autowired
    Mailer mailer

    // dates

    public static final LocalDateTime THREE_DAYS_AGO = DateHandler.now().minusDays(3)
    public static final LocalDateTime TWO_DAYS_AGO = DateHandler.now().minusDays(2)
    public static final LocalDateTime ONE_DAY_AGO = DateHandler.now().minusDays(1)
    public static final LocalDateTime NOW = DateHandler.now()
    public static final LocalDateTime IN_ONE_DAY = DateHandler.now().plusDays(1)
    public static final LocalDateTime IN_TWO_DAYS = DateHandler.now().plusDays(2)
    public static final LocalDateTime IN_THREE_DAYS = DateHandler.now().plusDays(3)
    public static final LocalDateTime IN_SIX_DAYS = DateHandler.now().plusDays(6)
    public static final LocalDateTime IN_EIGHT_DAYS = DateHandler.now().plusDays(8)
    public static final LocalDateTime IN_TEN_DAYS = DateHandler.now().plusDays(10)
    public static final LocalDateTime IN_TWELVE_DAYS = DateHandler.now().plusDays(12)

    // institution

    public static final String INSTITUTION_1_EMAIL = "institution1@mail.com"
    public static final String INSTITUTION_1_NAME = "institution1"
    public static final String INSTITUTION_1_NIF = "123456789"
    public static final String INSTITUTION_2_EMAIL = "institution2@mail.com"
    public static final String INSTITUTION_2_NAME = "institution2"
    public static final String INSTITUTION_2_NIF = "012345678"

    @Autowired
    InstitutionService institutionService

    @Autowired
    InstitutionRepository institutionRepository

    def createInstitution(String name, String email, String nif) {
        def institution = new Institution(name, email, nif)
        institutionRepository.save(institution)
        return institution
    }

    // login and demo

    public static final String ROLE_VOLUNTEER = "ROLE_VOLUNTEER"
    public static final String ROLE_MEMBER = "ROLE_MEMBER"
    public static final String ROLE_ADMIN = "ROLE_ADMIN"

    public static final String USER_1_NAME = "User 1 Name"
    public static final String USER_2_NAME = "User 2 Name"
    public static final String USER_3_NAME = "User 3 Name"
    public static final String USER_4_NAME = "User 4 Name"
    public static final String USER_5_NAME = "User 5 Name"
    public static final String USER_6_NAME = "User 6 Name"
    public static final String USER_7_NAME = "User 7 Name"
    public static final String USER_8_NAME = "User 8 Name"
    public static final String USER_9_NAME = "User 9 Name"
    public static final String USER_1_USERNAME = "rfs"
    public static final String USER_2_USERNAME = "jps"
    public static final String USER_3_USERNAME = "amm"
    public static final String USER_4_USERNAME = "user4"
    public static final String USER_5_USERNAME = "user5"
    public static final String USER_6_USERNAME = "user6"
    public static final String USER_7_USERNAME = "user7"
    public static final String USER_8_USERNAME = "user8"
    public static final String USER_9_USERNAME = "user9"
    public static final String USER_1_EMAIL = "user1@mail.com"
    public static final String USER_2_EMAIL = "user2@mail.com"
    public static final String USER_3_EMAIL = "user3@mail.com"
    public static final String USER_4_EMAIL = "user4@mail.com"
    public static final String USER_5_EMAIL = "user5@mail.com"
    public static final String USER_6_EMAIL = "user6@mail.com"
    public static final String USER_7_EMAIL = "user7@mail.com"
    public static final String USER_8_EMAIL = "user8@mail.com"
    public static final String USER_9_EMAIL = "user9@mail.com"
    public static final String USER_1_PASSWORD = "1234@WS4544"
    public static final String USER_2_PASSWORD = "4321@7877578"
    public static final String USER_3_PASSWORD = "4321@7877579"
    public static final String USER_4_PASSWORD = "4321@7877579"
    public static final String USER_5_PASSWORD = "4321@7877579"
    public static final String USER_1_TOKEN = "1a2b3c"
    public static final String USER_2_TOKEN = "c3b2a1"

    @Autowired
    AuthUserService authUserService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    AuthUserRepository authUserRepository

    @Autowired
    UserApplicationalService userServiceApplicational

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    DemoService demoService

    @Autowired
    DemoUtils demoUtils

    def demoVolunteerLogin() {
        def result = webClient.get()
                .uri('/auth/demo/volunteer')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        headers.setBearerAuth(result.token)

        return result.getUser()
    }

    def demoMemberLogin() {
        def result = webClient.get()
                .uri('/auth/demo/member')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        headers.setBearerAuth(result.token)

        return result.getUser()
    }

    def demoAdminLogin() {
        def result = webClient.get()
                .uri('/auth/demo/admin')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        headers.setBearerAuth(result.token)

        return result.getUser()
    }

    def normalUserLogin(username, password) {
        def result = webClient.post()
                .uri('/auth/user')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(new AuthPasswordDto(username, password))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        headers.setBearerAuth(result.token)

        return result.getUser()
    }

    def createMember(name, userName, password, email, type, institution, state) {
        def member = new Member(name, userName, email, type, institution, state)
        member.getAuthUser().setPassword(passwordEncoder.encode(password))
        userRepository.save(member)
        return member
    }

    def createVolunteer(name, userName, email, type, state) {
        def volunteer = new Volunteer(name, userName, email, type, state)
        userRepository.save(volunteer)
        return volunteer
    }

    def createVolunteerWithPassword(name, userName, password, email, type, state) {
        def volunteer = new Volunteer(name, userName, email, type, state)
        volunteer.getAuthUser().setPassword(passwordEncoder.encode(password))
        userRepository.save(volunteer)
        return volunteer
    }

    // theme

    public static final String THEME_NAME_1 = "THEME_NAME 1"
    public static final String THEME_NAME_2 = "THEME_NAME 2"

    @Autowired
    ThemeRepository themeRepository

    @Autowired
    ThemeService themeService

    def createTheme(name, type, parent) {
        def theme = new Theme(name, type, parent)
        themeRepository.save(theme)
        theme
    }

    // activity

    public static final String ACTIVITY_NAME_1 = "activity name 1"
    public static final String ACTIVITY_NAME_2 = "activity name 2"
    public static final String ACTIVITY_NAME_3 = "activity name 3"
    public static final String ACTIVITY_REGION_1 = "activity region 1"
    public static final String ACTIVITY_REGION_2 = "activity region 2"
    public static final String ACTIVITY_REGION_3 = "activity region 3"
    public static final String ACTIVITY_DESCRIPTION_1 = "activity description 1"
    public static final String ACTIVITY_DESCRIPTION_2 = "activity description 2"
    public static final String ACTIVITY_DESCRIPTION_3 = "activity description 3"
    public static final String ACTIVITY_SUSPENSION_JUSTIFICATION_VALID = "This is a valid justification."

    @Autowired
    ActivityRepository activityRepository

    @Autowired
    ActivityService activityService

    def createActivityDto(name, region, number, description, deadline, start, end, themesDto) {
        def activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)
        activityDto.setParticipantsNumberLimit(number)
        activityDto.setDescription(description)
        activityDto.setStartingDate(DateHandler.toISOString(start))
        activityDto.setEndingDate(DateHandler.toISOString(end))
        activityDto.setApplicationDeadline(DateHandler.toISOString(deadline))
        activityDto.setThemes(themesDto)
        activityDto
    }

    // activity suggestion
    @Autowired
    ActivitySuggestionRepository activitySuggestionRepository

    @Autowired
    ActivitySuggestionService activitySuggestionService


    def createActivitySuggestionDto(name, region, number, description, deadline, start, end) {
        def activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setName(name)
        activitySuggestionDto.setRegion(region)
        activitySuggestionDto.setParticipantsNumberLimit(number)
        activitySuggestionDto.setDescription(description)
        activitySuggestionDto.setStartingDate(DateHandler.toISOString(start))
        activitySuggestionDto.setEndingDate(DateHandler.toISOString(end))
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(deadline))
        activitySuggestionDto
    }

    def getObjectId(objectId, object) {
        if (objectId == EXIST)
            return object.id
        else if (objectId == NO_EXIST)
            return 222
        return null
    }

    // enrollment

    public static final String ENROLLMENT_MOTIVATION_1 = "enrollment motivation 1"
    public static final String ENROLLMENT_MOTIVATION_2 = "enrollment motivation 2"

    @Autowired
    EnrollmentService enrollmentService
    @Autowired
    EnrollmentRepository enrollmentRepository

    def createEnrollment(activity, volunteer, motivation) {
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(motivation)
        def enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)
        return enrollment
    }

    // participation

    public static final int MAX_REVIEW_LENGTH = 100
    public static final String MEMBER_REVIEW = "The volunteer did an excellent job."
    public static final String VOLUNTEER_REVIEW = "The activity was fun."

    def createParticipation(activity, volunteer, participationDto ) {
        participationDto.volunteerId = volunteer.getId()
        def participation = new Participation(activity, volunteer, participationDto)
        participationRepository.save(participation)
        return participation
    }
    @Autowired
    ParticipationService participationService
    @Autowired
    ParticipationRepository participationRepository


    // assessment

    public static final String ASSESSMENT_REVIEW_1 = "assessment review 1"
    public static final String ASSESSMENT_REVIEW_2 = "assessment review 2"

    @Autowired
    AssessmentService assessmentService
    @Autowired
    AssessmentRepository assessmentRepository

    def createAssessment(institution, volunteer, review) {
        def assessmentDto = new AssessmentDto()
        assessmentDto.setReview(review)
        def assessment = new Assessment(institution, volunteer, assessmentDto)
        assessmentRepository.save(assessment)
        return assessment
    }

    // volunteer profile

    public static final String VOLUNTEER_SHORT_BIO = "This is a volunteer's valid short bio"

    @Autowired
    VolunteerProfileService volunteerProfileService
    @Autowired
    VolunteerProfileRepository volunteerProfileRepository

    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    public static final String EXIST_NO_PROFILE = 'existButNoProfile'

    def getProfileObjectId(objectId, objectWithProfile, objectWithoutProfile=null) {
        if (objectId == EXIST)
            return objectWithProfile.id
        else if (objectId == NO_EXIST)
            return 222
        else if (objectId == EXIST_NO_PROFILE)
            return objectWithoutProfile.id
        else
            return null
    }

    def getExistingOrNullDto(value, volunteerProfileDto) {
        if (value == EXIST) {
            return volunteerProfileDto
        }
        return null
    }

    // institution profile

    public static final String INSTITUTION_SHORT_DESC = "This is an institution's valid short description"

    @Autowired
    InstitutionProfileService institutionProfileService
    @Autowired
    InstitutionProfileRepository institutionProfileRepository

    // report

    public static final String REPORT_JUSTIFICATION_1 = "report justification 1"
    public static final String REPORT_JUSTIFICATION_2 = "report justification 2"

    @Autowired
    ReportService reportService
    @Autowired
    ReportRepository reportRepository

    def createReport(activity, volunteer, justification) {
        def reportDto = new ReportDto()
        reportDto.setJustification(justification)
        def report = new Report(activity, volunteer, reportDto)
        reportRepository.save(report)
        return report
    }

    // clean database

    def deleteAll() {
        assessmentRepository.deleteAll()
        participationRepository.deleteAll()
        enrollmentRepository.deleteAll()
        volunteerProfileRepository.deleteAll()
        institutionProfileRepository.deleteAll()
        reportRepository.deleteAll()
        activityRepository.deleteAllActivityTheme()
        activityRepository.deleteAll()
        activitySuggestionRepository.deleteAll()
        authUserRepository.deleteAll()
        userRepository.deleteAll()
        institutionRepository.deleteAll()
        themeRepository.deleteAll()
    }


}
