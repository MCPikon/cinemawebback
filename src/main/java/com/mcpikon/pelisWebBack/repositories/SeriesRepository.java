package com.mcpikon.pelisWebBack.repositories;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.entities.Series;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeriesRepository extends MongoRepository<Series, ObjectId> {
    Optional<Movie> findByImdbId(String imdbId);
    boolean existsByImdbId(String imdbId);
}
