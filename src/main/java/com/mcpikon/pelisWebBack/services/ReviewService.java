package com.mcpikon.pelisWebBack.services;

import com.mcpikon.pelisWebBack.entities.Review;
import com.mcpikon.pelisWebBack.models.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewService {
    List<Review> findAll() throws ErrorException;
    List<Review> findAllByImdbId(String imdbId) throws ErrorException;
    Optional<Review> findById(ObjectId id) throws ErrorException;
    Review save(String title, String body, String imdbId) throws ErrorException;
    Map<String, String> delete(ObjectId id) throws ErrorException;
    Review update(ObjectId id, String title, String body) throws ErrorException;
}
