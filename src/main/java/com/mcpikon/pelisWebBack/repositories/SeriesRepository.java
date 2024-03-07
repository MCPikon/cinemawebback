package com.mcpikon.pelisWebBack.repositories;

import com.mcpikon.pelisWebBack.models.Series;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeriesRepository extends MongoRepository<Series, ObjectId> {
    Optional<Series> findByImdbId(String imdbId);
    boolean existsByImdbId(String imdbId);
}
