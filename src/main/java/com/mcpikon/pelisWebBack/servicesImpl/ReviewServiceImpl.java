package com.mcpikon.pelisWebBack.servicesImpl;

import com.mcpikon.pelisWebBack.entities.Movie;
import com.mcpikon.pelisWebBack.entities.Review;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.Errors;
import com.mcpikon.pelisWebBack.repositories.MovieRepository;
import com.mcpikon.pelisWebBack.repositories.ReviewRepository;
import com.mcpikon.pelisWebBack.services.ReviewService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Review> findAll() throws ErrorException {
        List<Review> reviews = reviewRepo.findAll();
        if (reviews.isEmpty()) throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);

        return reviews;
    }

    @Override
    public List<Review> findAllByImdbId(String imdbId) throws ErrorException {
        if (!movieRepo.existsByImdbId(imdbId)) throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);

        Optional<Movie> movie = movieRepo.findByImdbId(imdbId);
        List<Review> reviews = movie.get().getReviewIds();

        if (reviews.isEmpty()) throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);

        return reviews;
    }

    @Override
    public Optional<Review> findById(ObjectId id) throws ErrorException {
        return Optional.ofNullable(reviewRepo.findById(id).orElseThrow(() -> new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND)));
    }


    @Override
    public Review save(String body, String imdbId) {
        if (!movieRepo.existsByImdbId(imdbId)) throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);

        Review review = reviewRepo.insert(new Review(body));
        mongoTemplate.update(Movie.class)
                .matching(Criteria.where("imdbId").is(imdbId))
                .apply(new Update().push("reviewIds").value(review)).first();

        return review;
    }

    // TODO: Crear método para eliminar una review
}
