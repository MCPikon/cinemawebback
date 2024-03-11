package com.mcpikon.pelisWebBack.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.pelisWebBack.dtos.ReviewDTO;
import com.mcpikon.pelisWebBack.dtos.ReviewSaveDTO;
import com.mcpikon.pelisWebBack.models.Review;
import com.mcpikon.pelisWebBack.exceptions.ErrorException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewService {
    List<Review> findAll() throws ErrorException;
    List<Review> findAllByImdbId(String imdbId) throws ErrorException;
    Optional<Review> findById(ObjectId id) throws ErrorException;
    Review save(ReviewSaveDTO reviewSaveDTO) throws ErrorException;
    Map<String, String> delete(ObjectId id) throws ErrorException;
    Review update(ObjectId id, ReviewDTO reviewDTO) throws ErrorException;
    Review patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException;
}
