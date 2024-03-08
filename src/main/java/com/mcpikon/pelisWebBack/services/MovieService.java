package com.mcpikon.pelisWebBack.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.pelisWebBack.dtos.MovieDTO;
import com.mcpikon.pelisWebBack.models.Movie;
import com.mcpikon.pelisWebBack.exceptions.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MovieService {
    List<Movie> findAll() throws ErrorException;
    Optional<Movie> findById(ObjectId id) throws ErrorException;
    Optional<Movie> findByImdbId(String imdbId) throws ErrorException;
    Movie save(MovieDTO movieDTO) throws ErrorException;
    Map<String, String> delete(ObjectId id) throws ErrorException;
    Movie update(ObjectId id, MovieDTO movieDTO) throws ErrorException;
    Movie patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException;
}
