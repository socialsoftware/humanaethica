package pt.ulisboa.tecnico.socialsoftware.humanaethica.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;

@Component
public class DemoUtils {
    public static String DEMO = "DEMO";
    public static String INSTITUTION = "INSTITUTION";

    public static String DEMO_VOLUNTEER = DEMO + "-" + User.UserTypes.VOLUNTEER;

    public static String DEMO_MEMBER = DEMO + "-" + User.UserTypes.MEMBER;

    public static String DEMO_ADMIN = DEMO + "-" + User.UserTypes.ADMIN;

    public static String DEMO_INSTITUTION_NIF = "000000000";

    public static String DEMO_INSTITUTION = "DEMO INSTITUTION";

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
