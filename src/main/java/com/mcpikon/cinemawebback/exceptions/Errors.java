package com.mcpikon.cinemawebback.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum Errors {
    EMPTY(1L, "Empty List", HttpStatus.NO_CONTENT),
    NOT_FOUND(2L, "Entity not found", HttpStatus.NOT_FOUND),
    NOT_EXISTS(3L, "Entity doesn't exists", HttpStatus.NOT_FOUND),
    ALREADY_EXISTS(4L, "Entity already exists", HttpStatus.BAD_REQUEST),
    ID_CANNOT_CHANGE(5L, "ID key cannot be changed", HttpStatus.BAD_REQUEST),
    CANNOT_PARSE_OBJ_ID(6L,"Error parsing String id to ObjectId (id not valid)", HttpStatus.BAD_REQUEST),
    IMDB_ID_ALREADY_IN_USE(7L,"The imdbId passed is already in use", HttpStatus.BAD_REQUEST),
    CANNOT_PARSE_JSON(8L, "Cannot parse JSON Patch, change JSON object", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(9L, "Validation failed, check that the fields are not empty or null", HttpStatus.BAD_REQUEST);

    private final long id;
    private final String message;
    private final HttpStatus httpStatus;
}
