package pt.ulisboa.tecnico.socialsoftware.humanaethica.common.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "pt.ulisboa.tecnico.socialsoftware.humanaethica.common"})
@EntityScan(basePackages = {
        "pt.ulisboa.tecnico.socialsoftware.humanaethica.common"})
public class CommonModuleConfiguration {
}
