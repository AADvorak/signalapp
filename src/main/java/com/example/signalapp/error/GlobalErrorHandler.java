package com.example.signalapp.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError handleValidation(MethodArgumentNotValidException exc) {
        final MyError error = new MyError();
        exc.getBindingResult().getFieldErrors().forEach(fieldError-> {
            error.getErrors().add(String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()));
        });
        exc.getBindingResult().getGlobalErrors().forEach(err-> {
            error.getErrors().add(String.format("global: %s", err.getDefaultMessage()));
        });
        return error;
    }

    @ExceptionHandler(DataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError handleDataException(DataException exc) {
        final MyError error = new MyError();
        error.getErrors().add(exc.getErrorCode().getDescription());
        return error;
    }

    public static class MyError {

        private List<String> errors = new ArrayList<>();

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

    }
}

