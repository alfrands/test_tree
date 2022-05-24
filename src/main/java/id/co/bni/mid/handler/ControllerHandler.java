package id.co.bni.mid.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.SocketTimeoutException;
import java.sql.SQLSyntaxErrorException;

@ControllerAdvice(basePackages = "id.co.bni.mid")
public class ControllerHandler extends ResponseEntityExceptionHandler {

    private static Logger loggerHandler = LoggerFactory.getLogger(ControllerHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponseBuilder error = ValidationHandlerBuilder.fromBindingErrors(exception.getBindingResult());
        loggerHandler.warn(exception.getMessage());
        loggerHandler.error(exception.getMessage(), exception);
        return super.handleExceptionInternal(exception, error, headers, status, request);
    }

    @ExceptionHandler({JsonProcessingException.class, ParseException.class})
    public final ResponseEntity<Object> handleFailedSendIsoException(Exception ex, WebRequest request) {
        ErrorResponseBuilder error = new ErrorResponseBuilder(ex.getMessage());
        error.addErrorResponse(ex.getLocalizedMessage());
        loggerHandler.warn(ex.getMessage());
        loggerHandler.error(ex.getMessage(), ex);
        return new ResponseEntity(error, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public final ResponseEntity<Object> handleSocketTimeOutException(Exception ex, WebRequest request) {
        ErrorResponseBuilder error = new ErrorResponseBuilder(ex.getMessage());
        error.addErrorResponse(ex.getLocalizedMessage());
        loggerHandler.warn(ex.getMessage());
        loggerHandler.error(ex.getMessage(), ex);
        return new ResponseEntity(error, HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public final ResponseEntity<Object> handleSQLSyntaxException(Exception ex, WebRequest request) {
        ErrorResponseBuilder error = new ErrorResponseBuilder(ex.getMessage());
        error.addErrorResponse(ex.getLocalizedMessage());
        loggerHandler.warn(ex.getMessage());
        loggerHandler.error(ex.getMessage(), ex);
        return new ResponseEntity(error, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        ErrorResponseBuilder error = new ErrorResponseBuilder(ex.getMessage());
        error.addErrorResponse(ex.getLocalizedMessage());
        loggerHandler.warn(ex.getMessage());
        loggerHandler.error(ex.getMessage(), ex);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
