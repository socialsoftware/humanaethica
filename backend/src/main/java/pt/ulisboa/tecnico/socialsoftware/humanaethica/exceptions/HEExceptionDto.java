package pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

interface HEExceptionSubError extends Serializable {
}

public class HEExceptionDto implements HEExceptionSubError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime timestamp;

    private String message;

    private String debugMessage;

    private List<HEExceptionSubError> subErrors;


    HEExceptionDto(Throwable ex) {
        this.timestamp = DateHandler.now();
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public HEExceptionDto(HEException e) {
        this.timestamp = DateHandler.now();
        this.message = e.getMessage();
    }

    public HEExceptionDto(ErrorMessage errorMessage) {
        this.timestamp = DateHandler.now();
        this.message = errorMessage.label;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public List<HEExceptionSubError> getSubErrors() {
        return subErrors;
    }

    public void setSubErrors(List<HEExceptionSubError> subErrors) {
        this.subErrors = subErrors;
    }
}