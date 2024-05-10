package com.mcpikon.cinemawebback.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.SeriesDTO;
import com.mcpikon.cinemawebback.dtos.SeriesResponseDTO;
import com.mcpikon.cinemawebback.models.Series;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SeriesService {
    List<SeriesResponseDTO> findAll() throws ErrorException;
    List<SeriesResponseDTO> findAllByTitle(String title) throws ErrorException;
    Optional<Series> findById(ObjectId id) throws ErrorException;
    Optional<Series> findByImdbId(String imdbId) throws ErrorException;
    Series save(SeriesDTO seriesDTO) throws ErrorException;
    Map<String, String> delete(ObjectId id) throws ErrorException;
    Series update(ObjectId id, SeriesDTO seriesDTO) throws ErrorException;
    Series patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException;
}
