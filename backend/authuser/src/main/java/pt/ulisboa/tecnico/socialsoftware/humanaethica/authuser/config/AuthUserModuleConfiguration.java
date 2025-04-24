package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.config.CommonModuleConfiguration;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.config.MonolithicModuleConfiguration;

@Configuration
@ComponentScan(basePackages = {"pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser"})
@EnableJpaRepositories(basePackages = {"pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser"})
@EntityScan(basePackages = {"pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser"})
@Import({CommonModuleConfiguration.class, MonolithicModuleConfiguration.class})
public class AuthUserModuleConfiguration {
}
