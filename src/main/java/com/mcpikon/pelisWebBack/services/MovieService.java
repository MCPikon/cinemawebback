package com.mcpikon.pelisWebBack.services;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.models.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MovieService {
    List<Movie> findAll() throws ErrorException;
    Optional<Movie> findById(ObjectId id) throws ErrorException;
    Optional<Movie> findByImdbId(String imdbId) throws ErrorException;
    Movie save(Movie movie) throws ErrorException;
    Map<String, String> delete(ObjectId id) throws ErrorException;
    Movie update(Movie movie) throws ErrorException;
    Movie patch(ObjectId id, Map<String, String> fields) throws ErrorException;
}
