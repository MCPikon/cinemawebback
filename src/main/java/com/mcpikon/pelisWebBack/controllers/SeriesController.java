package com.mcpikon.pelisWebBack.controllers;

import com.mcpikon.pelisWebBack.entities.Series;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.ResponseBase;
import com.mcpikon.pelisWebBack.services.SeriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // TODO: Implementar métodos para los demás endpoints faltantes
}
