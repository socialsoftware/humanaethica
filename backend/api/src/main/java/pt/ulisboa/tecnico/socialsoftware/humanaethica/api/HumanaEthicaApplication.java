package pt.ulisboa.tecnico.socialsoftware.humanaethica.api;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.config.CommonModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.config.UserModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.JwtTokenProvider;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.config.MonolithicModuleConfiguration;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.demo.DemoUtils;

@SpringBootApplication
@EnableScheduling
@Import({MonolithicModuleConfiguration.class, CommonModuleConfiguration.class, UserModuleConfiguration.class})
public class HumanaEthicaApplication extends SpringBootServletInitializer implements InitializingBean {
    @Autowired
    private DemoUtils demoUtils;

    public static void main(String[] args) {
        SpringApplication.run(HumanaEthicaApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() {
        // Run on startup
        JwtTokenProvider.generateKeys();
        demoUtils.populateDemo();
    }
}
