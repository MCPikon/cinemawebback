package com.mcpikon.pelisWebBack.repositories;

import com.mcpikon.pelisWebBack.entities.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> { }
