package pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions;

public enum ErrorMessage {
    INVALID_TYPE_FOR_AUTH_USER("Invalid type for auth user"),
    INVALID_AUTH_USERNAME("Username: %s, belongs to a different authentication method"),
    USERNAME_ALREADY_EXIST("Username: %s, already exist"),
    INVALID_EMAIL("The mail %s is invalid."),
    INVALID_PASSWORD("The password %s is invalid."),
    INVALID_ROLE("The Role %s is invalid."),
    AUTHUSER_NOT_FOUND("AuthUser not found with id %d"),
    USER_NOT_FOUND("User not found with username %s"),
    COURSE_NOT_FOUND("Course not found with name %s"),
    INVALID_COURSE("The course you are exporting from does not match the course you are importing to."
            + "If you want to import anyway, edit the .xml file and replace the second line to: %s"),
    USER_ALREADY_ACTIVE("User is already active with username %s"),
    USER_IS_ACTIVE("User state is active: username %s"),
    INVALID_CONFIRMATION_TOKEN("Invalid confirmation token"),
    EXPIRED_CONFIRMATION_TOKEN("Expired confirmation token"),
    INVALID_LOGIN_CREDENTIALS("Invalid login credentials"),
    DUPLICATE_USER("Duplicate user: %s"),
    ACCESS_DENIED("You do not have permission to view this resource");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}