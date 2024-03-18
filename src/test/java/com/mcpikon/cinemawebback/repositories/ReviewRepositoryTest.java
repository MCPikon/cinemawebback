package com.mcpikon.cinemawebback.repositories;

import com.mcpikon.cinemawebback.models.Review;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewRepositoryTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withExposedPorts(27017);

    @Autowired
    private ReviewRepository reviewRepo;

    @Test
    @Order(0)
    @DisplayName("Find All Reviews - Empty List")
    void findAll_shouldGetEmpty() {
        assertEquals(0, reviewRepo.findAll().size());
    }

    @Test
    @Order(1)
    @DisplayName("Find All Reviews")
    void findAll_shouldNotEmpty_and_getTwoReviews() {
        reviewRepo.saveAll(new ArrayList<>(Arrays.asList(
                Review.builder().title("review 1").build(),
                Review.builder().title("review 2").build())));
        List<Review> reviewList = reviewRepo.findAll();
        assertFalse(reviewList.isEmpty());
        assertEquals(2, reviewList.size());
    }

    @Test
    @Order(2)
    @DisplayName("Save a single Review")
    void save_thenReturnSavedReview() {
        Review review = Review.builder().title("test").body("a review test").build();
        Review savedReview = reviewRepo.insert(review);
        assertNotNull(savedReview);
    }

    @Test
    @Order(3)
    @DisplayName("Find By Id")
    void findById_shouldNotEmpty() {
        Review reviewSaved = reviewRepo.insert(Review.builder()
                .title("review to find").build());
        Review reviewFounded = reviewRepo.findById(reviewSaved.getId()).orElseThrow();
        assertEquals(reviewSaved, reviewFounded);
    }

    @Test
    @Order(4)
    @DisplayName("Find By Id - Throw Not Found")
    void findById_shouldThrowNotFound() {
        Optional<Review> reviewFounded = reviewRepo.findById(new ObjectId());
        assertThrows(NoSuchElementException.class, reviewFounded::orElseThrow);
    }

    @Test
    @Order(5)
    @DisplayName("Delete a single Review")
    void delete_shouldGetOk() {
        Review reviewToDelete = reviewRepo.insert(Review.builder().title("review to delete").build());
        reviewRepo.delete(reviewToDelete);
        Optional<Review> reviewFounded = reviewRepo.findById(reviewToDelete.getId());
        assertThrows(NoSuchElementException.class, reviewFounded::orElseThrow);
    }

    @Test
    @Order(6)
    @DisplayName("Exists by Id - True")
    void existsById_shouldGetTrue() {
        Review reviewSaved = reviewRepo.insert(Review.builder().title("review exists test").build());
        assertTrue(reviewRepo.existsById(reviewSaved.getId()));
    }

    @Test
    @Order(7)
    @DisplayName("Exists by Id - False")
    void existsById_shouldGetFalse() {
        assertFalse(reviewRepo.existsById(new ObjectId()));
    }
}