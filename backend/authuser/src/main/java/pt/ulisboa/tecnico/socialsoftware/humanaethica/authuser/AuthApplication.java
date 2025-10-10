package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.config.CommonModuleConfiguration;


@PropertySource({"classpath:application.properties" })
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser"})
@EntityScan(basePackages = {"pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser"})
@Import({CommonModuleConfiguration.class})
@EnableDiscoveryClient
public class AuthApplication {


    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }


}
