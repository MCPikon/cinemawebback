package com.mcpikon.pelisWebBack.controllers;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.ResponseBase;
import com.mcpikon.pelisWebBack.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Movies", description = "Movies management API endpoints.")
@Slf4j
@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Operation(summary = "Fetch all movies", description = "fetches all movies and their data from data source")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(array = @ArraySchema(schema = @Schema(implementation = Movie.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "204", description = "Empty List")
    })
    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        try {
            return new ResponseEntity<>(movieService.findAll(), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in movies /findAll [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Fetch movie by id", description = "fetch a movie and their data filtering by id key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Movie.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(movieService.findById(id), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in movies /findById with id: \"%s\" [%s]", id, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Fetch movie by ImdbId", description = "fetch a movie and their data filtering by ImdbId key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Movie.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/findByImdbId/{imdbId}")
    public ResponseEntity<?> findByImdbId(@PathVariable String imdbId) {
        try {
            return new ResponseEntity<>(movieService.findByImdbId(imdbId), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in movies /findById with imdbId: \"%s\" [%s]", imdbId, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }
}