package com.mcpikon.pelisWebBack.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ErrorExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ResponseBase> handleErrorException(ErrorException e) {
        return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
    }

    @ExceptionHandler(JsonPatchException.class)
    public ResponseEntity<ResponseBase> handleJsonPatchException(JsonPatchException ex) {
        ErrorException e = new ErrorException(Errors.CANNOT_PARSE_JSON, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new ResponseBase(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ResponseBase> handleJsonProcessingException(JsonProcessingException ex) {
        ErrorException e = new ErrorException(Errors.CANNOT_PARSE_JSON, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new ResponseBase(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseBase> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorException e = new ErrorException(Errors.CANNOT_PARSE_OBJ_ID, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new ResponseBase(e), HttpStatus.BAD_REQUEST);
    }
}
