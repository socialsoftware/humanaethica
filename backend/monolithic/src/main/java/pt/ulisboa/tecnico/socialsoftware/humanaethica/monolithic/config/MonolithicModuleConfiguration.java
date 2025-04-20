package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.config.CommonModuleConfiguration;

@Configuration
@ComponentScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic")
@EntityScan(basePackages = "pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic")
@EnableJpaRepositories(basePackages = "pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic")
@Import({CommonModuleConfiguration.class})
public class MonolithicModuleConfiguration {
}