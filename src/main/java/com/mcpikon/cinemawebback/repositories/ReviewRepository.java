package com.mcpikon.cinemawebback.repositories;

import com.mcpikon.cinemawebback.models.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> { }
