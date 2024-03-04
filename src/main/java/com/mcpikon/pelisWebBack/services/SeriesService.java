package com.mcpikon.pelisWebBack.services;

import com.mcpikon.pelisWebBack.entities.Series;
import com.mcpikon.pelisWebBack.models.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SeriesService {
    List<Series> findAll() throws ErrorException;
    Optional<Series> findById(ObjectId id) throws ErrorException;
    Optional<Series> findByImdbId(String imdbId) throws ErrorException;
    Series save(Series series) throws ErrorException;
    Map<String, String> delete(ObjectId id) throws ErrorException;
    Series update(Series series) throws ErrorException;
    Series patch(ObjectId id, Map<String, String> fields) throws ErrorException;
}
