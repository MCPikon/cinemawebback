package com.mcpikon.pelisWebBack.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.pelisWebBack.dtos.SeriesDTO;
import com.mcpikon.pelisWebBack.models.Series;
import com.mcpikon.pelisWebBack.exceptions.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SeriesService {
    List<Series> findAll() throws ErrorException;
    Optional<Series> findById(ObjectId id) throws ErrorException;
    Optional<Series> findByImdbId(String imdbId) throws ErrorException;
    Series save(SeriesDTO seriesDTO) throws ErrorException;
    Map<String, String> delete(ObjectId id) throws ErrorException;
    Series update(ObjectId id, SeriesDTO seriesDTO) throws ErrorException;
    Series patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException;
}
