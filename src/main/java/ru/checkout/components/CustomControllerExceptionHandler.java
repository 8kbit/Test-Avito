package ru.checkout.components;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CustomControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler
    public ResponseEntity<Object> handleSQLException(Exception e) {
        return new ResponseEntity("don't do this anymore!!!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
