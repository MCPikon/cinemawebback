package com.mcpikon.pelisWebBack.controllers;

import com.mcpikon.pelisWebBack.entities.Series;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.ResponseBase;
import com.mcpikon.pelisWebBack.services.SeriesService;
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

@Tag(name = "Series", description = "Series management API endpoints.")
@Slf4j
@RestController
@RequestMapping("/api/v1/series")
public class SeriesController {
    @Autowired
    private SeriesService seriesService;

    @Operation(summary = "Fetch all series", description = "fetches all series and their data from data source")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(array = @ArraySchema(schema = @Schema(implementation = Series.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "204", description = "Empty List")
    })
    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        try {
            return new ResponseEntity<>(seriesService.findAll(), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in series /findAll [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Fetch series by id", description = "fetch a series and their data filtering by id key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(seriesService.findById(id), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in series /findById with id: '%s' [%s]", id, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Fetch series by ImdbId", description = "fetch a series and their data filtering by ImdbId key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/findByImdbId/{imdbId}")
    public ResponseEntity<?> findByImdbId(@PathVariable String imdbId) {
        try {
            return new ResponseEntity<>(seriesService.findByImdbId(imdbId), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in series /findById with imdbId: '%s' [%s]", imdbId, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Post new series", description = "Post new series into the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (A series or movie with the ImdbId passed already exists)")
    })
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Series series) {
        try {
            return new ResponseEntity<>(seriesService.save(series), HttpStatus.CREATED);
        } catch (ErrorException e) {
            log.error(String.format("Error in series /save [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Delete series by id", description = "Delete series with the id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Json.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The series with the id passed doesn't exists)")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(seriesService.delete(id), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in series /delete [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Update series by id", description = "Update a series with the id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The series with the id passed doesn't exists)")
    })
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Series series) {
        try {
            return new ResponseEntity<>(seriesService.update(series), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in series /update [%s]", e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }

    @Operation(summary = "Patch series by id", description = "Patch a series with the fields and id key passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = { @Content(schema = @Schema(implementation = Series.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request (The series with the id passed doesn't exists)")
    })
    @PatchMapping("/patch/{id}")
    public ResponseEntity<?> patch(@PathVariable ObjectId id, @RequestBody Map<String, String> fields) {
        try {
            return new ResponseEntity<>(seriesService.patch(id, fields), HttpStatus.OK);
        } catch (ErrorException e) {
            log.error(String.format("Error in series /patch with id: '%s' [%s]", id, e.getIdStatus()));
            return new ResponseEntity<>(new ResponseBase(e), e.getIdStatus());
        }
    }
}
