package com.mcpikon.cinemawebback.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.ReviewDTO;
import com.mcpikon.cinemawebback.dtos.ReviewSaveDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Movie;
import com.mcpikon.cinemawebback.models.Review;
import com.mcpikon.cinemawebback.models.Series;
import com.mcpikon.cinemawebback.repositories.MovieRepository;
import com.mcpikon.cinemawebback.repositories.ReviewRepository;
import com.mcpikon.cinemawebback.repositories.SeriesRepository;
import com.mcpikon.cinemawebback.services.ReviewService;
import com.mcpikon.cinemawebback.utils.DTOMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mcpikon.cinemawebback.exceptions.Errors.*;

@Slf4j
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private SeriesRepository seriesRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Review> findAll() throws ErrorException {
        log.info("GET reviews /findAll executed");
        List<Review> reviews = reviewRepo.findAll();
        if (reviews.isEmpty()) {
            log.warn(String.format("Warn in reviews /findAll [%s]", EMPTY.getMessage()));
            throw new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus());
        }
        return reviews;
    }

    @Override
    public List<Review> findAllByImdbId(String imdbId) throws ErrorException {
        log.info("GET reviews /findAllByImdbId executed");
        List<Review> reviews;

        if (movieRepo.existsByImdbId(imdbId)) reviews = movieRepo.findByImdbId(imdbId).orElseThrow().getReviewIds();
        else if (seriesRepo.existsByImdbId(imdbId)) reviews = seriesRepo.findByImdbId(imdbId).orElseThrow().getReviewIds();
        else {
            log.error(String.format("Error in reviews /findAllByImdbId with imdbId: '%s' [%s]", imdbId, NOT_EXISTS.getMessage()));
            throw new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        }

        if (reviews.isEmpty()) {
            log.warn(String.format("Warn in reviews /findAllByImdbId with imdbId: '%s' [%s]", imdbId, EMPTY.getMessage()));
            throw new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus());
        }
        return reviews;
    }

    @Override
    public Optional<Review> findById(ObjectId id) throws ErrorException {
        log.info("GET reviews /findById executed");
        return Optional.ofNullable(reviewRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in reviews /findById with id: '%s' [%s]", id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        }));
    }

    @Override
    public Review save(ReviewSaveDTO reviewSaveDTO) {
        log.info("POST reviews /save executed");
        final String imdbIdKey = "imdbId";
        final String reviewIdsKey = "reviewIds";
        Review review;
        Review reviewToSave;

        if (movieRepo.existsByImdbId(reviewSaveDTO.imdbId())) {
            reviewToSave = DTOMapper.dtoToReview(reviewSaveDTO);
            review = reviewRepo.insert(reviewToSave);
            mongoTemplate.update(Movie.class)
                    .matching(Criteria.where(imdbIdKey).is(reviewSaveDTO.imdbId()))
                    .apply(new Update().push(reviewIdsKey).value(review)).first();
        } else if (seriesRepo.existsByImdbId(reviewSaveDTO.imdbId())) {
            reviewToSave = DTOMapper.dtoToReview(reviewSaveDTO);
            review = reviewRepo.insert(reviewToSave);
            mongoTemplate.update(Series.class)
                    .matching(Criteria.where(imdbIdKey).is(reviewSaveDTO.imdbId()))
                    .apply(new Update().push(reviewIdsKey).value(review)).first();
        } else {
            log.error(String.format("Error in reviews /save with imdbId: '%s' [%s]", reviewSaveDTO.imdbId(), NOT_EXISTS.getMessage()));
            throw new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        }

        return review;
    }

    @Override
    public Map<String, String> delete(ObjectId id) {
        log.info("DELETE reviews /delete executed");
        Review reviewToDelete = reviewRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in reviews /delete with id: '%s' [%s]", id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        });
        reviewRepo.delete(reviewToDelete);
        return Map.of("message", String.format("Review with id: '%s' was successfully deleted", id));
    }

    @Override
    public Review update(ObjectId id, ReviewDTO reviewDTO) throws ErrorException {
        log.info("PUT reviews /update executed");
        Review reviewToFind = reviewRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in reviews /update with id: '%s' [%s]", id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        });
        Review reviewToUpdate = DTOMapper.dtoToReviewUpdate(reviewToFind, reviewDTO);
        return reviewRepo.save(reviewToUpdate);
    }

    @Override
    public Review patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException {
        log.info("PATCH reviews /patch executed");
        final String errorLogMsg = "Error in reviews /patch with id: '%s' [%s]";

        Review reviewToPatch = reviewRepo.findById(id).orElseThrow(() -> {
            log.error(String.format(errorLogMsg, id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        });
        var jsonPatchList = objectMapper.convertValue(jsonPatch, JsonNode.class);
        String path = jsonPatchList.get(0).get("path").asText();
        if (path.equalsIgnoreCase("/id")) {
            log.error(String.format(errorLogMsg, id, ID_CANNOT_CHANGE.getMessage()));
            throw new ErrorException(ID_CANNOT_CHANGE.getId(), ID_CANNOT_CHANGE.getMessage(), ID_CANNOT_CHANGE.getHttpStatus());
        }

        reviewToPatch.setUpdatedAt(LocalDateTime.now());
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(reviewToPatch, JsonNode.class));
        return reviewRepo.save(objectMapper.treeToValue(patched, Review.class));
    }
}
