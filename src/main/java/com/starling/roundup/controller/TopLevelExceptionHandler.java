package com.starling.roundup.controller;

import com.starling.roundup.service.exception.BusinessLogicException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice
public class TopLevelExceptionHandler {

    @ExceptionHandler({BusinessLogicException.class})
    public ResponseEntity<String> handleBusinessLogicException(BusinessLogicException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler({RestClientResponseException.class})
    public ResponseEntity<String> handleDownstreamException(RestClientResponseException exception) {
        if (exception.getRawStatusCode() == 403) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden - validate 'Access-Token' header");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    public ResponseEntity<String> handleMissingHeaderException(MissingRequestHeaderException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing header '" + exception.getHeaderName() + "'");
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleTopLevelException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
