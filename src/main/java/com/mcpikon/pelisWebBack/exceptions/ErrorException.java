package com.mcpikon.pelisWebBack.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class ErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7358330923614115585L;

    private final Long id;
    private final String message;
    private final HttpStatus httpStatus;

    public ErrorException(Long id, String message, HttpStatus httpStatus) {
        this.id = id;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
