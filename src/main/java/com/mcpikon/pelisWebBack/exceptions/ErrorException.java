package com.mcpikon.pelisWebBack.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
public class ErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7358330923614115585L;

    private Long id;
    private String message;
    private HttpStatus idStatus;

    public ErrorException(Errors error, HttpStatus idStatus) {
        super();
        this.id = error.getId();
        this.message = error.getMessage();
        this.idStatus = idStatus;
    }
}
