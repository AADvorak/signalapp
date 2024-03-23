package link.signalapp.error;

import link.signalapp.dto.response.ErrorDtoResponse;
import link.signalapp.dto.response.FieldErrorDtoResponse;
import link.signalapp.error.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(SignalAppNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleSignalAppNotFoundException() {
    }

    @ExceptionHandler(SignalAppUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleSignalAppUnauthorizedException() {
    }

    @ExceptionHandler(SignalAppAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleSignalAppAccessException() {
    }

    @ExceptionHandler(SignalAppDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<FieldErrorDtoResponse> handleSignalAppDataException(SignalAppDataException exc) {
        List<FieldErrorDtoResponse> errors = new ArrayList<>();
        if (exc.getErrorCode().getFields().isEmpty()) {
            errors.add(new FieldErrorDtoResponse()
                    .setCode(exc.getErrorCode().toString())
                    .setMessage(exc.getErrorCode().getDescription()));
        } else {
            exc.getErrorCode().getFields().forEach(field -> errors.add(new FieldErrorDtoResponse()
                    .setCode(exc.getErrorCode().toString())
                    .setField(field)
                    .setMessage(exc.getErrorCode().getDescription())));
        }
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public List<FieldErrorDtoResponse> handleBindException(BindException exc) {
        return makeFieldErrors(exc);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<FieldErrorDtoResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        return makeFieldErrors(exc);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void handleHttpRequestMethodNotSupportedException() {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public List<ErrorDtoResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return List.of(new ErrorDtoResponse()
                .setCode("INVALID_MEDIA_TYPE")
                .setMessage(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public List<ErrorDtoResponse> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        return List.of(new ErrorDtoResponse()
                .setCode("MEDIA_TYPE_NOT_ACCEPTABLE")
                .setMessage(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public List<ErrorDtoResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return List.of(new ErrorDtoResponse()
                .setCode("HTTP_MSG_NOT_READABLE")
                .setMessage(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public List<ErrorDtoResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return List.of(new ErrorDtoResponse()
                .setCode("METHOD_ARGUMENT_TYPE_MISMATCH")
                .setMessage(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public List<FieldErrorDtoResponse> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return List.of(new FieldErrorDtoResponse()
                .setCode("MISSING_REQUEST_PART")
                .setField(e.getRequestPartName())
                .setMessage(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public List<ErrorDtoResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return List.of(new ErrorDtoResponse()
                .setCode("NOT_FOUND")
                .setMessage(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SignalAppConflictException.class)
    public List<ErrorDtoResponse> handleSignalAppConflictException(SignalAppConflictException e) {
        return List.of(new ErrorDtoResponse()
                .setCode(e.getErrorCode().name())
                .setMessage(e.getErrorCode().getDescription())
                .setParams(e.getParams()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SignalAppException.class)
    public List<ErrorDtoResponse> handleSignalAppException(SignalAppException e) {
        return List.of(new ErrorDtoResponse()
                .setCode(e.getErrorCode().name())
                .setMessage(e.getErrorCode().getDescription())
                .setParams(e.getParams()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public List<ErrorDtoResponse> handleException(Exception e) {
        return List.of(new ErrorDtoResponse()
                .setCode("INTERNAL_SERVER_ERROR")
                .setMessage(e.getMessage()));
    }

    private List<FieldErrorDtoResponse> makeFieldErrors(BindException exc) {
        List<FieldErrorDtoResponse> errors = new ArrayList<>();
        exc.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.add(new FieldErrorDtoResponse()
                        .setCode(fieldError.getCode())
                        .setField(fieldError.getField())
                        .setMessage(fieldError.getDefaultMessage())));
        exc.getBindingResult().getGlobalErrors().forEach(err ->
                errors.add(new FieldErrorDtoResponse()
                        .setCode(err.getCode())
                        .setField(err.getObjectName())
                        .setMessage(err.getDefaultMessage())));
        return errors;
    }

}
