package com.mcpikon.pelisWebBack.servicesImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.pelisWebBack.models.Review;
import com.mcpikon.pelisWebBack.models.Series;
import com.mcpikon.pelisWebBack.exceptions.ErrorException;
import com.mcpikon.pelisWebBack.exceptions.Errors;
import com.mcpikon.pelisWebBack.repositories.MovieRepository;
import com.mcpikon.pelisWebBack.repositories.ReviewRepository;
import com.mcpikon.pelisWebBack.repositories.SeriesRepository;
import com.mcpikon.pelisWebBack.services.SeriesService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class SeriesServiceImpl implements SeriesService {
    @Autowired
    private SeriesRepository seriesRepo;

    @Autowired
    private MovieRepository moviesRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Series> findAll() throws ErrorException {
        log.info("GET series /findAll executed");
        List<Series> seriesList = seriesRepo.findAll();
        if (seriesList.isEmpty()) {
            log.error(String.format("Error in series /findAll [%s]", HttpStatus.NO_CONTENT));
            throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);
        }
        return seriesList;
    }

    @Override
    public Optional<Series> findById(ObjectId id) throws ErrorException {
        log.info("GET series /findById executed");
        return Optional.ofNullable(seriesRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in series /findById with id: '%s' [%s]", id, HttpStatus.NOT_FOUND));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND);
        }));
    }

    @Override
    public Optional<Series> findByImdbId(String imdbId) throws ErrorException {
        log.info("GET series /findByImdbId executed");
        return Optional.ofNullable(seriesRepo.findByImdbId(imdbId).orElseThrow(() -> {
            log.error(String.format("Error in series /findByImdbId with imdbId: '%s' [%s]", imdbId, HttpStatus.NOT_FOUND));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND);
        }));
    }

    @Override
    public Series save(Series series) throws ErrorException {
        log.info("POST series /save executed");
        if (seriesRepo.existsByImdbId(series.getImdbId()) || moviesRepo.existsByImdbId(series.getImdbId())) {
            log.error(String.format("Error in series /save with imdbId: '%s' [%s]", series.getImdbId(), HttpStatus.BAD_REQUEST));
            throw new ErrorException(Errors.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        series.setReviewIds(new ArrayList<>());
        return seriesRepo.insert(series);
    }

    @Override
    public Map<String, String> delete(ObjectId id) throws ErrorException {
        log.info("DELETE series /delete executed");
        Series seriesToDelete = seriesRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in series /delete with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        });
        for (Review review : seriesToDelete.getReviewIds()) {
            reviewRepo.delete(review);
        }
        seriesRepo.delete(seriesToDelete);
        return Map.of("message", String.format("Series with id: '%s' was successfully deleted", id));
    }

    @Override
    public Series update(Series series) throws ErrorException {
        log.info("PUT series /update executed");
        if (!seriesRepo.existsById(series.getId())) {
            log.error(String.format("Error in series /update with id: '%s' [%s]", series.getId(), HttpStatus.BAD_REQUEST));
            throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        }
        return seriesRepo.save(series);
    }

    @Override
    public Series patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException {
        log.info("PATCH series /patch executed");
        Series seriesToPatch = seriesRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in series /patch with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        });
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(seriesToPatch, JsonNode.class));
        return seriesRepo.save(objectMapper.treeToValue(patched, Series.class));
    }
}
