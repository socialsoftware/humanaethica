package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser", "pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic"})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser", "pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic"})
@ComponentScan({"pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser"," pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic"})
public class AuthUserTestConfiguration {
}
