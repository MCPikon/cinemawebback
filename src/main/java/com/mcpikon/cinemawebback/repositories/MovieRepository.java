package com.mcpikon.cinemawebback.repositories;

import com.mcpikon.cinemawebback.models.Movie;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, ObjectId> {
    Optional<Movie> findByImdbId(String imdbId);
    boolean existsByImdbId(String imdbId);
    @Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
    Page<Movie> findAllByTitle(String title, Pageable pageable);
}