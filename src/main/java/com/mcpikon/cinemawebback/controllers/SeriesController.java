package com.mcpikon.cinemawebback.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.SeriesDTO;
import com.mcpikon.cinemawebback.dtos.SeriesResponseDTO;
import com.mcpikon.cinemawebback.models.Series;
import com.mcpikon.cinemawebback.services.SeriesService;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "Series", description = "Series management API endpoints.")
@RestController
@RequestMapping("/api/v1/series")
public class SeriesController {
    @Autowired
    private SeriesService seriesService;

    @Operation(summary = "Fetch all series", description = "fetches all series and their data from data source")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(array = @ArraySchema(schema = @Schema(implementation = SeriesResponseDTO.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "204", description = "Empty List")
    })
    @GetMapping("/findAll")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(required = false) String title, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(seriesService.findAll(title, page, size), HttpStatus.OK);
    }

    @Operation(summary = "Fetch series by id", description = "fetch a series and their data filtering by id key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/findById/{id}")
    public ResponseEntity<Optional<Series>> findById(@PathVariable ObjectId id) {
        return new ResponseEntity<>(seriesService.findById(id), HttpStatus.OK);
    }

    @Operation(summary = "Fetch series by ImdbId", description = "fetch a series and their data filtering by ImdbId key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/findByImdbId/{imdbId}")
    public ResponseEntity<Optional<Series>> findByImdbId(@PathVariable String imdbId) {
        return new ResponseEntity<>(seriesService.findByImdbId(imdbId), HttpStatus.OK);
    }

    @Operation(summary = "Post new series", description = "Post new series into the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (A series or movie with the ImdbId passed already exists)")
    })
    @PostMapping("/save")
    public ResponseEntity<Series> save(@Valid @RequestBody SeriesDTO seriesDTO) {
        return new ResponseEntity<>(seriesService.save(seriesDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete series by id", description = "Delete series with the id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Json.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Exists")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable ObjectId id) {
        return new ResponseEntity<>(seriesService.delete(id), HttpStatus.OK);
    }

    @Operation(summary = "Update series by id", description = "Update a series with the id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Exists"),
            @ApiResponse(responseCode = "400", description = "Bad Request (The ImdbId passed is already in use)")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<Series> update(@PathVariable ObjectId id, @Valid @RequestBody SeriesDTO seriesDTO) {
        return new ResponseEntity<>(seriesService.update(id, seriesDTO), HttpStatus.OK);
    }

    @Operation(summary = "Patch series by id", description = "Patch a series with the fields and id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Exists"),
            @ApiResponse(responseCode = "400", description = "Bad Request (The Id key cannot be changed)"),
            @ApiResponse(responseCode = "400", description = "Bad Request (The ImdbId passed is already in use)")
    })
    @PatchMapping("/patch/{id}")
    public ResponseEntity<Series> patch(@PathVariable ObjectId id, @RequestBody JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        return new ResponseEntity<>(seriesService.patch(id, jsonPatch), HttpStatus.OK);
    }
}