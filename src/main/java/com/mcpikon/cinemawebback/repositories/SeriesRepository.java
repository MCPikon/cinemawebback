package com.mcpikon.cinemawebback.repositories;

import com.mcpikon.cinemawebback.models.Series;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends MongoRepository<Series, ObjectId> {
    Optional<Series> findByImdbId(String imdbId);
    boolean existsByImdbId(String imdbId);
    @Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
    List<Series> findAllByTitle(String title);
}
