package com.mcpikon.pelisWebBack.repositories;

import com.mcpikon.pelisWebBack.models.Movie;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, ObjectId> {
    Optional<Movie> findByImdbId(String imdbId);
    boolean existsByImdbId(String imdbId);
}