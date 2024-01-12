package com.mcpikon.pelisWebBack.services;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.models.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> findAll() throws ErrorException;
    Optional<Movie> findById(ObjectId id) throws ErrorException;
    Optional<Movie> findByImdbId(String imdbId) throws ErrorException;
}
