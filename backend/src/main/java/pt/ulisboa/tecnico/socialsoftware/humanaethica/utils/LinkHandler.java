package pt.ulisboa.tecnico.socialsoftware.humanaethica.utils;

public class LinkHandler {

    private static final String BASE = "http:localhost:8081";

    private LinkHandler() {
    }

    public static String createConfirmRegistrationLink(String username, String token) {
        String format = "/register/confirmation?username=%s&token=%s";
        return BASE + String.format(format, username, token);
    }


}
