package pt.ulisboa.tecnico.socialsoftware.humanaethica;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories({"pt.ulisboa.tecnico.socialsoftware.humanaethica"})
@EntityScan({"pt.ulisboa.tecnico.socialsoftware.humanaethica"})
public class HumanaEthicaTest {
}
