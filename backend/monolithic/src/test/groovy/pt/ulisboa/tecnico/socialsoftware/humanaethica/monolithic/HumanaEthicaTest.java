package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic"})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic"})
public class HumanaEthicaTest {
}
