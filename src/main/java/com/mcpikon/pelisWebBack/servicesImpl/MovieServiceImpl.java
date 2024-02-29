package com.mcpikon.pelisWebBack.servicesImpl;

import com.mcpikon.pelisWebBack.entities.Movie;
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
        return null;
    }

    @Override
    public Map<String, String> delete(ObjectId id) throws ErrorException {
        return null;
    }

    @Override
    public Movie update(ObjectId id, Movie movie) throws ErrorException {
        return null;
    }

    @Override
    public Movie patch(ObjectId id, Map<String, String> fields) throws ErrorException {
        return null;
    }
}
