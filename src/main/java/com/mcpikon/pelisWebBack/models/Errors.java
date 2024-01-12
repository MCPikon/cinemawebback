package com.mcpikon.pelisWebBack.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Errors {
    EMPTY(1L, "Empty List"),
    NOT_FOUND(2L, "Entity not found"),
    NOT_EXISTS(3L, "Entity doesn't exists");

    private long id;
    private String message;
}
