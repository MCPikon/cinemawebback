package com.mcpikon.pelisWebBack.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.pelisWebBack.dtos.MovieDTO;
import com.mcpikon.pelisWebBack.models.Movie;
import com.mcpikon.pelisWebBack.models.Review;
import com.mcpikon.pelisWebBack.exceptions.ErrorException;
import com.mcpikon.pelisWebBack.exceptions.Errors;
import com.mcpikon.pelisWebBack.repositories.MovieRepository;
import com.mcpikon.pelisWebBack.repositories.ReviewRepository;
import com.mcpikon.pelisWebBack.repositories.SeriesRepository;
import com.mcpikon.pelisWebBack.services.MovieService;
import com.mcpikon.pelisWebBack.utils.DTOMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public List<Movie> findAll() throws ErrorException {
        log.info("GET movies /findAll executed");
        List<Movie> movies = movieRepo.findAll();
        if (movies.isEmpty()) {
            log.error(String.format("Error in movies /findAll [%s]", HttpStatus.NO_CONTENT));
            throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);
        }
        return movies;
    }

    @Override
    public Optional<Movie> findById(ObjectId id) throws ErrorException {
        log.info("GET movies /findById executed");
        return Optional.ofNullable(movieRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in movies /findById with id: '%s' [%s]", id, HttpStatus.NOT_FOUND));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND);
        }));
    }

    @Override
    public Optional<Movie> findByImdbId(String imdbId) throws ErrorException {
        log.info("GET movies /findByImdbId executed");
        return Optional.ofNullable(movieRepo.findByImdbId(imdbId).orElseThrow(() -> {
            log.error(String.format("Error in movies /findByImdbId with imdbId: '%s' [%s]", imdbId, HttpStatus.NOT_FOUND));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND);
        }));
    }

    @Override
    public Movie save(MovieDTO movieDTO) throws ErrorException {
        log.info("POST movies /save executed");
        if (movieRepo.existsByImdbId(movieDTO.imdbId()) || seriesRepo.existsByImdbId(movieDTO.imdbId())) {
            log.error(String.format("Error in movies /save with imdbId: '%s' [%s]", movieDTO.imdbId(), HttpStatus.BAD_REQUEST));
            throw new ErrorException(Errors.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        Movie movieToSave = DTOMapper.dtoToMovie(movieDTO);
        return movieRepo.insert(movieToSave);
    }

    @Override
    public Map<String, String> delete(ObjectId id) throws ErrorException {
        log.info("DELETE movie /delete executed");
        Movie movieToDelete = movieRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in movies /delete with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
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
        Movie movieToFind = movieRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in movies /update with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        });
        if ((movieRepo.existsByImdbId(movieDTO.imdbId()) || seriesRepo.existsByImdbId(movieDTO.imdbId()))
                && !movieToFind.getImdbId().equalsIgnoreCase(movieDTO.imdbId())) {
            log.error(String.format("Error in movies /update with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            throw new ErrorException(Errors.IMDB_ID_ALREADY_IN_USE, HttpStatus.BAD_REQUEST);
        }
        Movie movieToUpdate = DTOMapper.dtoToMovieUpdate(movieToFind, movieDTO);
        return movieRepo.save(movieToUpdate);
    }

    @Override
    public Movie patch(ObjectId id, JsonPatch jsonPatch) throws ErrorException, JsonPatchException, JsonProcessingException {
        log.info("PATCH movies /patch executed");
        Movie movieToPatch = movieRepo.findById(id).orElseThrow(() -> {
            log.error(String.format("Error in movies /patch with id: '%s' [%s]", id, HttpStatus.BAD_REQUEST));
            return new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        });
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(movieToPatch, JsonNode.class));
        return movieRepo.save(objectMapper.treeToValue(patched, Movie.class));
    }
}