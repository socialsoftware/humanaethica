package pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions;

public enum ErrorMessage {
    INVALID_TYPE_FOR_AUTH_USER("Invalid type for auth user"),
    INVALID_AUTH_USERNAME("Username: %s, belongs to a different authentication method"),
    INVALID_INSTITUTION_NAME("Name: %s, is not valid"),
    INVALID_ACTIVITY_NAME("Name: %s, is not valid"),
    INVALID_REGION_NAME("Name: %s, is not valid"),
    USERNAME_ALREADY_EXIST("Username: %s, already exist"),
    NIF_ALREADY_EXIST("Institution with NIF: %s, already exist"),
    INVALID_EMAIL("The mail %s is invalid."),
    INVALID_NIF("The NIF %s is invalid."),
    INVALID_PASSWORD("The password %s is invalid."),
    INVALID_URL_FOR_DOCUMENT("Invalid url for document"),
    INVALID_ROLE("The Role %s is invalid."),
    INVALID_STATE("The State %s is invalid"),
    AUTHUSER_NOT_FOUND("AuthUser not found with id %d"),
    USER_NOT_FOUND("User not found with username %s"),
    INSTITUTION_NOT_FOUND("Institution not found with id %d"),
    ACTIVITY_NOT_FOUND("Activity not found with id %d"),
    USER_NOT_APPROVED("The member of this institution is not yet approved"),
    COURSE_NOT_FOUND("Course not found with name %s"),
    INVALID_COURSE("The course you are exporting from does not match the course you are importing to."
            + "If you want to import anyway, edit the .xml file and replace the second line to: %s"),
    USER_ALREADY_ACTIVE("User is already active with username %s"),
    USER_IS_ACTIVE("User state is active: username %s"),
    INSTITUTION_IS_ACTIVE("Institution state is active: name %s"),
    INVALID_CONFIRMATION_TOKEN("Invalid confirmation token"),
    EXPIRED_CONFIRMATION_TOKEN("Expired confirmation token"),
    INVALID_LOGIN_CREDENTIALS("Invalid login credentials"),
    DUPLICATE_USER("Duplicate user: %s"),
    INVALID_THEME_NAME("Name: %s, is not valid"),
    THEME_NOT_FOUND("Theme not found with id %d"),
    ACCESS_DENIED("You do not have permission to view this resource");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}