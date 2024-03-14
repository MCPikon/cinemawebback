package com.mcpikon.cinemawebback.repositories;

import com.mcpikon.cinemawebback.models.Movie;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, ObjectId> {
    Optional<Movie> findByImdbId(String imdbId);
    boolean existsByImdbId(String imdbId);
}