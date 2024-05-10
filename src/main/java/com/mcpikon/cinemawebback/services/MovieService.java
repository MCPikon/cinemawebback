package com.mcpikon.cinemawebback.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.MovieDTO;
import com.mcpikon.cinemawebback.dtos.MovieResponseDTO;
import com.mcpikon.cinemawebback.models.Movie;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MovieService {
    List<MovieResponseDTO> findAll() throws ErrorException;
    List<MovieResponseDTO> findAllByTitle(String title) throws ErrorException;
    Optional<Movie> findById(ObjectId id) throws ErrorException;
    Optional<Movie> findByImdbId(String imdbId) throws ErrorException;
    Movie save(MovieDTO movieDTO) throws ErrorException;
    Map<String, String> delete(ObjectId id) throws ErrorException;
    Movie update(ObjectId id, MovieDTO movieDTO) throws ErrorException;
    Movie patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException;
}
