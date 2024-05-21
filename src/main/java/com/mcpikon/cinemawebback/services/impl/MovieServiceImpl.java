package com.mcpikon.cinemawebback.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.MovieDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Movie;
import com.mcpikon.cinemawebback.models.Review;
import com.mcpikon.cinemawebback.repositories.MovieRepository;
import com.mcpikon.cinemawebback.repositories.ReviewRepository;
import com.mcpikon.cinemawebback.repositories.SeriesRepository;
import com.mcpikon.cinemawebback.services.MovieService;
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
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private SeriesRepository seriesRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> findAll(String title, int page, int size) throws ErrorException {
        log.info("GET movies /findAll executed");
        if (page < 0) page = 0;
        if (size <= 0) size = 1;

        Pageable paging = PageRequest.of(page, size);
        Page<Movie> movies;

        if (title == null) movies = movieRepo.findAll(paging);
        else movies = movieRepo.findAllByTitle(title, paging);

        if (movies.isEmpty()) {
            log.warn(String.format("Warn in movies /findAll [%s]", EMPTY.getMessage()));
            throw new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("movies", movies.getContent().stream().map(DTOMapper::movieToResponseDTO).toList());
        response.put("currentPage", movies.getNumber());
        response.put("totalItems", movies.getTotalElements());
        response.put("totalPages", movies.getTotalPages());

        return response;
    }

    @Override
    public Optional<Movie> findById(ObjectId id) throws ErrorException {
        log.info("GET movies /findById executed");
        return Optional.ofNullable(movieRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in movies /findById with id: '%s' [%s]", id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        }));
    }

    @Override
    public Optional<Movie> findByImdbId(String imdbId) throws ErrorException {
        log.info("GET movies /findByImdbId executed");
        return Optional.ofNullable(movieRepo.findByImdbId(imdbId).orElseThrow(() -> {
            log.error(String.format("Error in movies /findByImdbId with imdbId: '%s' [%s]", imdbId, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        }));
    }

    @Override
    public Movie save(MovieDTO movieDTO) throws ErrorException {
        log.info("POST movies /save executed");
        if (movieRepo.existsByImdbId(movieDTO.imdbId()) || seriesRepo.existsByImdbId(movieDTO.imdbId())) {
            log.error(String.format("Error in movies /save with imdbId: '%s' [%s]", movieDTO.imdbId(), ALREADY_EXISTS.getMessage()));
            throw new ErrorException(ALREADY_EXISTS.getId(), ALREADY_EXISTS.getMessage(), ALREADY_EXISTS.getHttpStatus());
        }
        Movie movieToSave = DTOMapper.dtoToMovie(movieDTO);
        return movieRepo.insert(movieToSave);
    }

    @Override
    public Map<String, String> delete(ObjectId id) throws ErrorException {
        log.info("DELETE movie /delete executed");
        Movie movieToDelete = movieRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in movies /delete with id: '%s' [%s]", id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        });
        for (Review review : movieToDelete.getReviewIds()) {
            reviewRepo.delete(review);
        }
        movieRepo.delete(movieToDelete);
        return Map.of("message", String.format("Movie with id: '%s' was successfully deleted", id));
    }

    @Override
    public Movie update(ObjectId id, MovieDTO movieDTO) throws ErrorException {
        log.info("PUT movie /update executed");
        final String errorLogMsg = "Error in movies /update with id: '%s' [%s]";
        Movie movieToFind = movieRepo.findById(id).orElseThrow(() -> {
            log.error(String.format(errorLogMsg, id, NOT_EXISTS.getMessage()));
            return new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus());
        });
        if ((movieRepo.existsByImdbId(movieDTO.imdbId()) || seriesRepo.existsByImdbId(movieDTO.imdbId()))
                && !movieToFind.getImdbId().equalsIgnoreCase(movieDTO.imdbId())) {
            log.error(String.format(errorLogMsg, id, IMDB_ID_ALREADY_IN_USE.getMessage()));
            throw new ErrorException(IMDB_ID_ALREADY_IN_USE.getId(), IMDB_ID_ALREADY_IN_USE.getMessage(), IMDB_ID_ALREADY_IN_USE.getHttpStatus());
        }
        Movie movieToUpdate = DTOMapper.dtoToMovieUpdate(movieToFind, movieDTO);
        return movieRepo.save(movieToUpdate);
    }

    @Override
    public Movie patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException {
        log.info("PATCH movies /patch executed");
        final String errorLogMsg = "Error in movies /patch with id: '%s' [%s]";

        Movie movieToPatch = movieRepo.findById(id).orElseThrow(() -> {
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
                && !movieToPatch.getImdbId().equalsIgnoreCase(value))) {
            log.error(String.format(errorLogMsg, id, IMDB_ID_ALREADY_IN_USE.getMessage()));
            throw new ErrorException(IMDB_ID_ALREADY_IN_USE.getId(), IMDB_ID_ALREADY_IN_USE.getMessage(), IMDB_ID_ALREADY_IN_USE.getHttpStatus());
        }

        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(movieToPatch, JsonNode.class));
        return movieRepo.save(objectMapper.treeToValue(patched, Movie.class));
    }
}