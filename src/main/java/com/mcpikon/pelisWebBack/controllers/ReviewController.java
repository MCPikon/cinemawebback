package com.mcpikon.pelisWebBack.controllers;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.entities.Review;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.ResponseBase;
import com.mcpikon.pelisWebBack.services.ReviewService;
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

@Slf4j
@Tag(name = "Reviews", description = "Reviews management API endpoints.")
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Fetch all reviews", description = "fetches all reviews and their data from data source")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(array = @ArraySchema(schema = @Schema(implementation = Review.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "204", description = "Empty List")
    })
    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        try {
            return new ResponseEntity<>(reviewService.findAll(), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in reviews /findAll [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Fetch all reviews by ImdbId", description = "fetches all reviews and their data from movie or series with the ImdbId key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(array = @ArraySchema(schema = @Schema(implementation = Review.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "204", description = "Empty List")
    })
    @GetMapping("/findAllByImdbId/{imdbId}")
    public ResponseEntity<?> findAllByImdbId(@PathVariable String imdbId) {
        try {
            return new ResponseEntity<>(reviewService.findAllByImdbId(imdbId), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in reviews /findAllByImdbId with imdbId: '%s' [%s]", imdbId, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Fetch review by id", description = "fetch a review and their data filtering by id key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Review.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(reviewService.findById(id), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in reviews /findById with id: '%s' [%s]", id, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Post review by ImdbId key", description = "Post a review and their data to the movie or series with the ImdbId key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = { @Content(schema = @Schema(implementation = Review.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (A series or movie with the ImdbId passed doesn't exists)")
    })
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Map<String, String> payload) {
        try {
            return new ResponseEntity<>(reviewService.save(payload.get("title"), payload.get("body"), payload.get("imdbId")), HttpStatus.CREATED);
        } catch (ErrorException e) {
            log.error(String.format("Error in reviews /save [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Delete review by id", description = "Delete a review and the id related in movies or series reviewIds array with the id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Json.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The review with the id passed doesn't exists)")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(reviewService.delete(id), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in reviews /delete [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Update review by id", description = "Update a review with the id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Review.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The api can't parse the String id to ObjectId)"),
            @ApiResponse(responseCode = "400", description = "Bad Request (The review with the id passed doesn't exists)")
    })
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, String> payload) {
        try {
            ObjectId id;
            try {
                id = new ObjectId(payload.get("id"));
            } catch (Exception e) {
                log.error("Error in reviews /update parsing String id to ObjectId");
                return new ResponseEntity<>(Map.of("message", "Error parsing String id to ObjectId (id not valid)"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(reviewService.update(id, payload.get("title"), payload.get("body")), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in reviews /update [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Patch review by id", description = "Patch a review with the fields and id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Review.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The review with the id passed doesn't exists)")
    })
    @PatchMapping("/patch/{id}")
    public ResponseEntity<?> patch(@PathVariable ObjectId id, @RequestBody Map<String, String> fields) {
        try {
            return new ResponseEntity<>(reviewService.patch(id, fields), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in reviews /patch with id: '%s' [%s]", id, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }
}
