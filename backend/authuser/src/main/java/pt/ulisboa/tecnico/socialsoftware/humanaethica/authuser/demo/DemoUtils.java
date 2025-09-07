package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;

@Component
public class DemoUtils {
    public static String DEMO = "DEMO";
    public static String INSTITUTION = "INSTITUTION";

    public static String DEMO_VOLUNTEER = DEMO + "-" + Role.VOLUNTEER;

    public static String DEMO_MEMBER = DEMO + "-" + Role.MEMBER;

    public static String DEMO_ADMIN = DEMO + "-" + Role.ADMIN;



    @Autowired
    Environment environment;

    @Autowired
    DemoService demoService;

    public void populateDemo() {
        if (environment.acceptsProfiles(Profiles.of("dev"))
                || environment.acceptsProfiles(Profiles.of("test-int"))) {
            demoService.getDemoAdmin();
        }

    }


}
