package pt.ulisboa.tecnico.socialsoftware.humanaethica

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.assessment.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.auth.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.config.HEPermissionEvaluator
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.demo.DemoService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.enrollment.EnrollmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.participation.ParticipationService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.report.ReportService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.user.UserApplicationalService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.activity.ActivityService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.theme.ThemeService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.theme.ThemeService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolitico.utils.Mailer

@TestConfiguration
@PropertySource("classpath:application-test-int.properties")
@ActiveProfiles("test")
class BeanConfiguration {

    @Value('${spring.mail.host}')
    private String host

    @Value('${spring.mail.port}')
    private int port

    @Value('${spring.mail.username}')
    private String username

    @Value('${spring.mail.password}')
    private String password

    @Value('${spring.mail.properties.mail.smtp.auth}')
    private String auth;

    @Value('${spring.mail.properties.mail.smtp.starttls.enable}')
    private String starttls

    @Value('${spring.mail.properties.mail.transport.protocol}')
    private String protocol

    @Value('${spring.mail.properties.mail.debug}')
    private String debug

    @Bean
    UserService userService() {
        return new UserService()
    }

    @Bean
    UserApplicationalService userServiceApplicational() {
        return new UserApplicationalService()
    }

    @Bean
    InstitutionService institutionService() {
        return new InstitutionService()
    }

    @Bean
    ThemeService themeService() {
        return new ThemeService()
    }

    @Bean
    ActivityService activityService() {
        return new ActivityService()
    }

    @Bean
    EnrollmentService enrollmentService() {
        return new EnrollmentService()
    }

    @Bean
    ParticipationService participationService() {
        return new ParticipationService()
    }

    @Bean
    AssessmentService assessmentService() {
        return new AssessmentService()

    }

    @Bean
    ReportService reportService() {
        return new ReportService()
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    HEPermissionEvaluator hePermissionEvaluator() {
        return new HEPermissionEvaluator();
    }

    @Bean
    AuthUserService authUserService() {
        return new AuthUserService()
    }

    @Bean
    DemoUtils demoUtils() {
        return new DemoUtils();
    }

    @Bean
    DemoService demoService() {
        return new DemoService();
    }

    @Bean
    Mailer mailer() {
        return new Mailer()
    }

    @Bean
    JavaMailSender getJavaMailSender() {
        JavaMailSender mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", Boolean.parseBoolean(protocol));
        props.put("mail.smtp.auth", Boolean.parseBoolean(auth));
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", debug);

        return mailSender;
    }
}