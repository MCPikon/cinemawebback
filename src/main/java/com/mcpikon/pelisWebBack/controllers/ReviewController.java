package com.mcpikon.pelisWebBack.controllers;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.entities.Review;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.ResponseBase;
import com.mcpikon.pelisWebBack.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Fetch all reviews by ImdbId", description = "fetches all reviews and their data from movie with the ImdbId key passed")
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
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Fetch review by id", description = "fetch a review and their data filtering by id key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Movie.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(reviewService.findById(id), HttpStatus.OK);
        } catch (ErrorException e) {
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Post review by movie ImdbId key", description = "Post a review and their data to the movie with the ImdbId key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = { @Content(schema = @Schema(implementation = Review.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The movie with the ImdbId passed doesn't exists)")
    })
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Map<String, String> payload) {
        try {
            return new ResponseEntity<>(reviewService.save(payload.get("body"), payload.get("imdbId")), HttpStatus.CREATED);
        } catch (ErrorException e) {
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }
}
