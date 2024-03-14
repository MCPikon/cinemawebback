package com.mcpikon.cinemawebback.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.mcpikon.cinemawebback.exceptions.Errors.CANNOT_PARSE_JSON;
import static com.mcpikon.cinemawebback.exceptions.Errors.CANNOT_PARSE_OBJ_ID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ResponseBase> handleErrorException(ErrorException e) {
        return new ResponseEntity<>(new ResponseBase(e), e.getHttpStatus());
    }

    @ExceptionHandler({JsonPatchException.class, JsonProcessingException.class})
    public ResponseEntity<ResponseBase> handleJsonPatchAndProcessingException() {
        final ErrorException e = new ErrorException(CANNOT_PARSE_JSON.getId(), CANNOT_PARSE_JSON.getMessage(), CANNOT_PARSE_JSON.getHttpStatus());
        return new ResponseEntity<>(new ResponseBase(e), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseBase> handleMethodArgumentTypeMismatchException() {
        final ErrorException e = new ErrorException(CANNOT_PARSE_OBJ_ID.getId(), CANNOT_PARSE_OBJ_ID.getMessage(), CANNOT_PARSE_OBJ_ID.getHttpStatus());
        return new ResponseEntity<>(new ResponseBase(e), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBase> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        final String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error(String.format("Error in validation [%s]", errorMsg));
        return new ResponseEntity<>(new ResponseBase(10L, errorMsg), HttpStatus.BAD_REQUEST);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ResponseBase {
        private Long id;
        private String message;

        public ResponseBase(ErrorException e) {
            this.id = e.getId();
            this.message = e.getMessage();
        }
    }
}
