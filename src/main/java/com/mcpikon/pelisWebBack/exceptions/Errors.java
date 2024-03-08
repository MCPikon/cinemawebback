package com.mcpikon.pelisWebBack.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Errors {
    EMPTY(1L, "Empty List"),
    NOT_FOUND(2L, "Entity not found"),
    NOT_EXISTS(3L, "Entity doesn't exists"),
    ALREADY_EXISTS(4L, "Entity already exists"),
    ID_CANNOT_CHANGE(5L, "ID key cannot be changed"),
    CANNOT_PARSE_OBJ_ID(6L,"Error parsing String id to ObjectId (id not valid)"),
    IMDB_ID_ALREADY_IN_USE(7L,"The imdbId passed is already in use"),
    CANNOT_PARSE_JSON(8L, "Cannot parse JSON Patch, change JSON object");

    private final long id;
    private final String message;
}
