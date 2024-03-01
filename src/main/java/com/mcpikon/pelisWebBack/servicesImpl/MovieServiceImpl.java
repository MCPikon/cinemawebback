package com.mcpikon.pelisWebBack.servicesImpl;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.entities.Review;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.Errors;
import com.mcpikon.pelisWebBack.repositories.MovieRepository;
import com.mcpikon.pelisWebBack.services.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepo;

    @Override
    public List<Movie> findAll() throws ErrorException {
        log.info("GET movies /findAll executed");
        List<Movie> movies = movieRepo.findAll();
        if (movies.isEmpty()) throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);
        return movies;
    }

    @Override
    public Optional<Movie> findById(ObjectId id) throws ErrorException {
        log.info("GET movies /findById executed");
        return Optional.ofNullable(movieRepo.findById(id).orElseThrow(() -> new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND)));
    }

    @Override
    public Optional<Movie> findByImdbId(String imdbId) throws ErrorException {
        log.info("GET movies /findByImdbId executed");
        return Optional.ofNullable(movieRepo.findByImdbId(imdbId).orElseThrow(() -> new ErrorException(Errors.NOT_FOUND, HttpStatus.NOT_FOUND)));
    }

    // TODO: Implementar métodos POST, DELETE, PUT y PATCH para Movies
    @Override
    public Movie save(Movie movie) throws ErrorException {
        log.info("POST movies /save executed");
        if (movieRepo.existsByImdbId(movie.getImdbId())) throw new ErrorException(Errors.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        movie.setReviewIds(new ArrayList<>());
        return movieRepo.insert(movie);
    }

    @Override
    public Map<String, String> delete(ObjectId id) throws ErrorException {
        log.info("DELETE movie /delete executed");
        if (!movieRepo.existsById(id)) throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        Optional<Movie> movieToDelete = movieRepo.findById(id);
        movieRepo.delete(movieToDelete.orElseThrow());
        return Map.of("message", String.format("Review with id: \"%s\" was successfully deleted", id));
    }

    @Override
    public Movie update(Movie movie) throws ErrorException {
        log.info("PUT movie /update executed");
        if (!movieRepo.existsById(movie.getId())) throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        return movieRepo.save(movie);
    }

    @Override
    public Movie patch(ObjectId id, Map<String, String> fields) throws ErrorException {
        log.info("PATCH movies /patch executed");
        if (!movieRepo.existsById(id)) throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        Movie movieToPatch = movieRepo.findById(id).orElseThrow();
        fields.forEach((key, value) -> {
            if (key.equalsIgnoreCase("id") || key.equalsIgnoreCase("imdbId")) {
                throw new ErrorException(Errors.ID_CANNOT_CHANGE, HttpStatus.BAD_REQUEST);
            }
            Field field = ReflectionUtils.findField(Review.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, movieToPatch, value);
            }
        });
        return movieRepo.save(movieToPatch);
    }
}
