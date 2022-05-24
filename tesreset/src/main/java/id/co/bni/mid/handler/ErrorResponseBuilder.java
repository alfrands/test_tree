package id.co.bni.mid.handler;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponseBuilder {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors = new ArrayList<>();

    private String data;
    private final String message;

    public ErrorResponseBuilder(String message) {
        this.message = message;
    }

    public ErrorResponseBuilder(String message, String data) {
        this.message = message;
        this.data = data;
    }

    public void addErrorResponse(String error) {
        errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
