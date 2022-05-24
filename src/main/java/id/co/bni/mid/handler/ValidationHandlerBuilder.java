package id.co.bni.mid.handler;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class ValidationHandlerBuilder {

    protected static ErrorResponseBuilder fromBindingErrors(Errors errors) {
        ErrorResponseBuilder error = new ErrorResponseBuilder("Validation failed. " + errors.getErrorCount() + " error(s)");
        for (ObjectError objectError : errors.getAllErrors()) {
            error.addErrorResponse(objectError.getDefaultMessage());
        }
        return error;
    }
}
