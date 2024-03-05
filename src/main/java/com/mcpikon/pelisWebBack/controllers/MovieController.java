package com.mcpikon.pelisWebBack.controllers;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.ResponseBase;
import com.mcpikon.pelisWebBack.services.MovieService;
import io.swagger.v3.core.util.Json;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            log.error(String.format("Error in movies /findById with id: '%s' [%s]", id, e.getIdStatus()));
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
            log.error(String.format("Error in movies /findById with imdbId: '%s' [%s]", imdbId, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Post new movie", description = "Post new movie into the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = { @Content(schema = @Schema(implementation = Movie.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (A movie or series with the ImdbId passed already exists)")
    })
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Movie movie) {
        try {
            return new ResponseEntity<>(movieService.save(movie), HttpStatus.CREATED);
        } catch (ErrorException e) {
            log.error(String.format("Error in movies /save [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Delete movie by id", description = "Delete movie with the id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Json.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The movie with the id passed doesn't exists)")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(movieService.delete(id), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in movies /delete [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Update movie by id", description = "Update a movie with the id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Movie.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The movie with the id passed doesn't exists)")
    })
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Movie movie) {
        try {
            return new ResponseEntity<>(movieService.update(movie), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in movies /update [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Patch movie by id", description = "Patch a movie with the fields and id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Movie.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The movie with the id passed doesn't exists)")
    })
    @PatchMapping("/patch/{id}")
    public ResponseEntity<?> patch(@PathVariable ObjectId id, @RequestBody Map<String, String> fields) {
        try {
            return new ResponseEntity<>(movieService.patch(id, fields), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in movies /patch with id: '%s' [%s]", id, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }
}