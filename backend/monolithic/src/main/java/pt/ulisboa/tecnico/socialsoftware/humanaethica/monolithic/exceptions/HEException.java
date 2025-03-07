package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HEException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(HEException.class);

    private final ErrorMessage errorMessage;

    public HEException(ErrorMessage errorMessage) {
        super(errorMessage.label);
        logger.error(errorMessage.label);
        this.errorMessage = errorMessage;
    }

    public HEException(ErrorMessage errorMessage, String value) {
        super(String.format(errorMessage.label, value));
        logger.error(String.format(errorMessage.label, value));
        this.errorMessage = errorMessage;
    }

    public HEException(ErrorMessage errorMessage, String value1, String value2) {
        super(String.format(errorMessage.label, value1, value2));
        logger.error(String.format(errorMessage.label, value1, value2));
        this.errorMessage = errorMessage;
    }

    public HEException(ErrorMessage errorMessage, int value) {
        super(String.format(errorMessage.label, value));
        logger.error(String.format(errorMessage.label, value));
        this.errorMessage = errorMessage;
    }

    public HEException(ErrorMessage errorMessage, int value1, int value2) {
        super(String.format(errorMessage.label, value1, value2));
        logger.error(String.format(errorMessage.label, value1, value2));
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
