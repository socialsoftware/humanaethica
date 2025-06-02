package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser

import com.google.common.eventbus.EventBus
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service.UserApplicationalService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.Mailer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService

@TestConfiguration
@PropertySource("classpath:application-test.properties")
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

    /*@Bean
    UserService userService() {
        return new UserService()
    }*/

    @Bean
    UserApplicationalService userServiceApplicational() {
        return new UserApplicationalService()
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
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

    @Bean
    EventBus eventBus() {
        return new EventBus()
    }
}