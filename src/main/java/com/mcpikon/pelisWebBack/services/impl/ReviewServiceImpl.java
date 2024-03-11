package com.mcpikon.pelisWebBack.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.pelisWebBack.dtos.ReviewDTO;
import com.mcpikon.pelisWebBack.dtos.ReviewSaveDTO;
import com.mcpikon.pelisWebBack.models.Movie;
import com.mcpikon.pelisWebBack.models.Review;
import com.mcpikon.pelisWebBack.models.Series;
import com.mcpikon.pelisWebBack.exceptions.ErrorException;
import com.mcpikon.pelisWebBack.exceptions.Errors;
import com.mcpikon.pelisWebBack.repositories.MovieRepository;
import com.mcpikon.pelisWebBack.repositories.ReviewRepository;
import com.mcpikon.pelisWebBack.repositories.SeriesRepository;
import com.mcpikon.pelisWebBack.services.ReviewService;
import com.mcpikon.pelisWebBack.utils.DTOMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            log.error(String.format("Error in reviews /findAll [%s]", HttpStatus.NO_CONTENT));
            throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);
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
            log.error(String.format("Error in reviews /findAllByImdbId with imdbId: '%s' [%s]", imdbId, HttpStatus.BAD_REQUEST));
            throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        }

        if (reviews.isEmpty()) {
            log.error(String.format("Error in reviews /findAllByImdbId with imdbId: '%s' [%s]", imdbId, HttpStatus.NO_CONTENT));
            throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);
        }
        return reviews;
    }

    @Override
    public Optional<Review> findById(ObjectId id) throws ErrorException {
        log.info("GET reviews /findById executed");
        return Optional.ofNullable(reviewRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in reviews /findById with id: '%s' [%s]", id, HttpStatus.NOT_FOUND));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND);
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
            log.error(String.format("Error in reviews /save with imdbId: '%s' [%s]", reviewSaveDTO.imdbId(), HttpStatus.BAD_REQUEST));
            throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        }

        return review;
    }

    @Override
    public Map<String, String> delete(ObjectId id) {
        log.info("DELETE reviews /delete executed");
        Review reviewToDelete = reviewRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in reviews /delete with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        });
        reviewRepo.delete(reviewToDelete);
        return Map.of("message", String.format("Review with id: '%s' was successfully deleted", id));
    }

    @Override
    public Review update(ObjectId id, ReviewDTO reviewDTO) throws ErrorException {
        log.info("PUT reviews /update executed");
        Review reviewToFind = reviewRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in reviews /update with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        });
        Review reviewToUpdate = DTOMapper.dtoToReviewUpdate(reviewToFind, reviewDTO);
        return reviewRepo.save(reviewToUpdate);
    }

    @Override
    public Review patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException {
        log.info("PATCH reviews /patch executed");
        Review reviewToPatch = reviewRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in reviews /patch with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        });
        var jsonPatchList = objectMapper.convertValue(jsonPatch, JsonNode.class);
        if (jsonPatchList.get(0).get("path").asText().equalsIgnoreCase("/id")) {
            log.error(String.format("Error in reviews /patch with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            throw new ErrorException(Errors.ID_CANNOT_CHANGE, HttpStatus.BAD_REQUEST);
        }
        reviewToPatch.setUpdatedAt(LocalDateTime.now());
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(reviewToPatch, JsonNode.class));
        return reviewRepo.save(objectMapper.treeToValue(patched, Review.class));
    }
}
