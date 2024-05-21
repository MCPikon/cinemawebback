package com.mcpikon.cinemawebback.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.SeriesDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Review;
import com.mcpikon.cinemawebback.models.Series;
import com.mcpikon.cinemawebback.repositories.MovieRepository;
import com.mcpikon.cinemawebback.repositories.ReviewRepository;
import com.mcpikon.cinemawebback.repositories.SeriesRepository;
import com.mcpikon.cinemawebback.services.SeriesService;
import com.mcpikon.cinemawebback.utils.DTOMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.mcpikon.cinemawebback.exceptions.Errors.*;

@Slf4j
@Service
@Transactional
public class SeriesServiceImpl implements SeriesService {
    @Autowired
    private SeriesRepository seriesRepo;

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> findAll(String title, int page, int size) throws ErrorException {
        log.info("GET series /findAll executed");
        if (page < 0) page = 0;
        if (size <= 0) size = 1;

        Pageable paging = PageRequest.of(page, size);
        Page<Series> series;

        if (title == null) series = seriesRepo.findAll(paging);
        else series = seriesRepo.findAllByTitle(title, paging);

        if (series.isEmpty()) {
            log.warn(String.format("Warn in series /findAll [%s]", EMPTY.getMessage()));
            throw new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("series", series.getContent().stream().map(DTOMapper::seriesToResponseDTO).toList());
        response.put("currentPage", series.getNumber());
        response.put("totalItems", series.getTotalElements());
        response.put("totalPages", series.getTotalPages());

        return response;
    }

    @Override
    public Optional<Series> findById(ObjectId id) throws ErrorException {
        log.info("GET series /findById executed");
        return Optional.ofNullable(seriesRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in series /findById with id: '%s' [%s]", id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        }));
    }

    @Override
    public Optional<Series> findByImdbId(String imdbId) throws ErrorException {
        log.info("GET series /findByImdbId executed");
        return Optional.ofNullable(seriesRepo.findByImdbId(imdbId).orElseThrow(() -> {
            log.error(String.format("Error in series /findByImdbId with imdbId: '%s' [%s]", imdbId, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        }));
    }

    @Override
    public Series save(SeriesDTO seriesDTO) throws ErrorException {
        log.info("POST series /save executed");
        if (seriesRepo.existsByImdbId(seriesDTO.imdbId()) || movieRepo.existsByImdbId(seriesDTO.imdbId())) {
            log.error(String.format("Error in series /save with imdbId: '%s' [%s]", seriesDTO.imdbId(), ALREADY_EXISTS.getMessage()));
            throw new ErrorException(ALREADY_EXISTS.getId(), ALREADY_EXISTS.getMessage(), ALREADY_EXISTS.getHttpStatus());
        }
        Series seriesToSave = DTOMapper.dtoToSeries(seriesDTO);
        return seriesRepo.insert(seriesToSave);
    }

    @Override
    public Map<String, String> delete(ObjectId id) throws ErrorException {
        log.info("DELETE series /delete executed");
        Series seriesToDelete = seriesRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in series /delete with id: '%s' [%s]", id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        });
        for (Review review : seriesToDelete.getReviewIds()) {
            reviewRepo.delete(review);
        }
        seriesRepo.delete(seriesToDelete);
        return Map.of("message", String.format("Series with id: '%s' was successfully deleted", id));
    }

    @Override
    public Series update(ObjectId id, SeriesDTO seriesDTO) throws ErrorException {
        log.info("PUT series /update executed");
        final String errorLogMsg = "Error in series /update with id: '%s' [%s]";
        Series seriesToFind = seriesRepo.findById(id).orElseThrow(() -> {
            log.error(String.format(errorLogMsg, id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        });
        if ((movieRepo.existsByImdbId(seriesDTO.imdbId()) || seriesRepo.existsByImdbId(seriesDTO.imdbId()))
                && !seriesToFind.getImdbId().equalsIgnoreCase(seriesDTO.imdbId())) {
            log.error(String.format(errorLogMsg, id, IMDB_ID_ALREADY_IN_USE.getMessage()));
            throw new ErrorException(IMDB_ID_ALREADY_IN_USE.getId(), IMDB_ID_ALREADY_IN_USE.getMessage(), IMDB_ID_ALREADY_IN_USE.getHttpStatus());
        }
        Series seriesToUpdate = DTOMapper.dtoToSeriesUpdate(seriesToFind, seriesDTO);
        return seriesRepo.save(seriesToUpdate);
    }

    @Override
    public Series patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException {
        log.info("PATCH series /patch executed");
        final String errorLogMsg = "Error in series /patch with id: '%s' [%s]";

        Series seriesToPatch = seriesRepo.findById(id).orElseThrow(() -> {
            log.error(String.format(errorLogMsg, id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        });
        var jsonPatchList = objectMapper.convertValue(jsonPatch, JsonNode.class);
        String path = jsonPatchList.get(0).get("path").asText();
        String value = jsonPatchList.get(0).get("value").asText();

        if (path.equalsIgnoreCase("/id")) {
            log.error(String.format(errorLogMsg, id, ID_CANNOT_CHANGE.getMessage()));
            throw new ErrorException(ID_CANNOT_CHANGE.getId(), ID_CANNOT_CHANGE.getMessage(), ID_CANNOT_CHANGE.getHttpStatus());
        } else if (path.equalsIgnoreCase("/imdbId")
                && ((movieRepo.existsByImdbId(value) || seriesRepo.existsByImdbId(value))
                && !seriesToPatch.getImdbId().equalsIgnoreCase(value))) {
            log.error(String.format(errorLogMsg, id, IMDB_ID_ALREADY_IN_USE.getMessage()));
            throw new ErrorException(IMDB_ID_ALREADY_IN_USE.getId(), IMDB_ID_ALREADY_IN_USE.getMessage(), IMDB_ID_ALREADY_IN_USE.getHttpStatus());
        }

        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(seriesToPatch, JsonNode.class));
        return seriesRepo.save(objectMapper.treeToValue(patched, Series.class));
    }
}
