package com.security.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    private ErrorMessage errorMessage;

    @ExceptionHandler(StudentException.class)
    public ResponseEntity<ErrorMessage> studentExceptionHandler(
            StudentException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if(exception instanceof EmailDuplicateException)
            status = HttpStatus.CONFLICT;
        if(exception instanceof StudentNotFoundException)
            status = HttpStatus.NOT_FOUND;

        errorMessage = new ErrorMessage(
                status.value(),
                exception.getMessage(),
                request.getDescription(false),
                new Date()
        );

        return new ResponseEntity<>(errorMessage, status);
    }
}
