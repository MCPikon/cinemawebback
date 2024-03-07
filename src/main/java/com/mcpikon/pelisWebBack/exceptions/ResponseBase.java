package com.mcpikon.pelisWebBack.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseBase {
    private Long id;
    private String message;

    public ResponseBase(ErrorException e) {
        this.id = e.getId();
        this.message = e.getMessage();
    }
}
