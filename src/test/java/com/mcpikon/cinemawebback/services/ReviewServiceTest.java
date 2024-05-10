package com.mcpikon.cinemawebback.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.ReviewDTO;
import com.mcpikon.cinemawebback.dtos.ReviewSaveDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Movie;
import com.mcpikon.cinemawebback.models.Review;
import com.mcpikon.cinemawebback.models.Series;
import com.mcpikon.cinemawebback.repositories.MovieRepository;
import com.mcpikon.cinemawebback.repositories.ReviewRepository;
import com.mcpikon.cinemawebback.repositories.SeriesRepository;
import com.mcpikon.cinemawebback.services.impl.ReviewServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.ExecutableUpdateOperation.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepo;

    @Mock
    private MovieRepository movieRepo;

    @Mock
    private SeriesRepository seriesRepo;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonPatch jsonPatch;

    @Mock
    private JsonNode jsonNode;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    @DisplayName("Find All Reviews - OK")
    void findAllReviews_thenReturnsList() {
        List<Review> reviewList = List.of(
                Review.builder().title("review 1").build(),
                Review.builder().title("review 2").build());
        when(reviewRepo.findAll()).thenReturn(reviewList);
        List<Review> reviewsFoundedList = reviewService.findAll();
        assertNotNull(reviewsFoundedList);
        assertEquals(reviewList.size(), reviewsFoundedList.size());
    }

    @Test
    @DisplayName("Find All Reviews - Throws Empty List")
    void findAllReviews_thenThrowsEmptyList() {
        when(reviewRepo.findAll()).thenReturn(new ArrayList<>());
        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.findAll(), "ErrorException was expected");
        assertEquals("Empty List", thrown.getMessage());
    }

    @Test
    @DisplayName("Find All Reviews By ImdbId - OK (Exists By Movie ImdbId)")
    void findAllReviewsByImdbId_whenExistsByMovieImdbId_thenReturnList() {
        String imdbId = "tt12345";
        List<Review> reviewList = List.of(
                Review.builder().title("review 1").build(),
                Review.builder().title("review 2").build());
        Movie movie = Movie.builder().imdbId(imdbId).reviewIds(reviewList).build();
        when(movieRepo.existsByImdbId(imdbId)).thenReturn(true);
        when(movieRepo.findByImdbId(imdbId)).thenReturn(Optional.of(movie));

        List<Review> reviewsFoundedList = reviewService.findAllByImdbId(imdbId);
        assertNotNull(reviewsFoundedList);
        assertEquals(reviewList.size(), reviewsFoundedList.size());
    }

    @Test
    @DisplayName("Find All Reviews By ImdbId - OK (Exists By Series ImdbId)")
    void findAllReviewsByImdbId_whenExistsBySeriesImdbId_thenReturnList() {
        String imdbId = "tt12345";
        List<Review> reviewList = List.of(
                Review.builder().title("review 1").build(),
                Review.builder().title("review 2").build());
        Series series = Series.builder().imdbId(imdbId).reviewIds(reviewList).build();
        when(seriesRepo.existsByImdbId(imdbId)).thenReturn(true);
        when(seriesRepo.findByImdbId(imdbId)).thenReturn(Optional.of(series));

        List<Review> reviewsFoundedList = reviewService.findAllByImdbId(imdbId);
        assertNotNull(reviewsFoundedList);
        assertEquals(reviewList.size(), reviewsFoundedList.size());
    }

    @Test
    @DisplayName("Find All Reviews By ImdbId - Throws Not Exists")
    void findAllReviewsByImdbId_thenThrowNotExists() {
        String imdbId = "tt54321";
        when(movieRepo.existsByImdbId(imdbId)).thenReturn(false);
        when(seriesRepo.existsByImdbId(imdbId)).thenReturn(false);
        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.findAllByImdbId(imdbId), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Find All Reviews By ImdbId - Throws Empty List")
    void findAllReviewsByImdbId_thenThrowsEmptyList() {
        String imdbId = "tt12345";
        Movie movie = Movie.builder().imdbId(imdbId).reviewIds(new ArrayList<>()).build();
        when(movieRepo.existsByImdbId(imdbId)).thenReturn(true);
        when(movieRepo.findByImdbId(imdbId)).thenReturn(Optional.of(movie));

        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.findAllByImdbId(imdbId), "ErrorException was expected");
        assertEquals("Empty List", thrown.getMessage());
    }

    @Test
    @DisplayName("Find Review By Id - OK")
    void findReviewById_thenReturnReview() {
        ObjectId id = new ObjectId();
        Review reviewGiven = Review.builder().id(id).title("movie to find").build();
        when(reviewRepo.findById(id)).thenReturn(Optional.of(reviewGiven));
        Review reviewFounded = reviewService.findById(id).orElseThrow();
        assertNotNull(reviewFounded);
        assertEquals(reviewGiven.getId(), reviewFounded.getId());
    }

    @Test
    @DisplayName("Find Review By Id - Throws Not Exists")
    void findReviewById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.findById(id), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Save Review (Movie Associated) - OK")
    void saveReviewAssociatedWithMovie_thenReturnsReview() {
        String imdbId = "tt54321";
        Review reviewGiven = Review.builder().title("review to save").build();
        ReviewSaveDTO reviewSaveDTO = ReviewSaveDTO.builder().title("review to save").imdbId(imdbId).build();

        when(movieRepo.existsByImdbId(imdbId)).thenReturn(true);
        when(reviewRepo.insert(any(Review.class))).thenReturn(reviewGiven);
        var ex = mock(ExecutableUpdate.class);
        var up = mock(UpdateWithUpdate.class);
        var tup = mock(TerminatingUpdate.class);
        when(mongoTemplate.update(Movie.class)).thenReturn(ex);
        when(ex.matching(Criteria.where("imdbId").is(reviewSaveDTO.imdbId()))).thenReturn(up);
        when(up.apply(any(Update.class))).thenReturn(tup);

        Review reviewSaved = reviewService.save(reviewSaveDTO);
        assertNotNull(reviewSaved);
    }

    @Test
    @DisplayName("Save Review (Series Associated) - OK")
    void saveReviewAssociatedWithSeries_thenReturnsReview() {
        String imdbId = "tt54321";
        Review reviewGiven = Review.builder().title("review to save").build();
        ReviewSaveDTO reviewSaveDTO = ReviewSaveDTO.builder().title("review to save").imdbId(imdbId).build();

        when(seriesRepo.existsByImdbId(imdbId)).thenReturn(true);
        when(reviewRepo.insert(any(Review.class))).thenReturn(reviewGiven);
        var ex = mock(ExecutableUpdate.class);
        var up = mock(UpdateWithUpdate.class);
        var tup = mock(TerminatingUpdate.class);
        when(mongoTemplate.update(Series.class)).thenReturn(ex);
        when(ex.matching(Criteria.where("imdbId").is(reviewSaveDTO.imdbId()))).thenReturn(up);
        when(up.apply(any(Update.class))).thenReturn(tup);

        Review reviewSaved = reviewService.save(reviewSaveDTO);
        assertNotNull(reviewSaved);
    }

    @Test
    @DisplayName("Save Review - Throws Not Exists")
    void saveReview_thenThrowsNotExists() {
        String imdbId = "tt12345";
        ReviewSaveDTO reviewSaveDTO = ReviewSaveDTO.builder().imdbId(imdbId).title("review to save").build();
        when(movieRepo.existsByImdbId(imdbId)).thenReturn(false);
        when(seriesRepo.existsByImdbId(imdbId)).thenReturn(false);
        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.save(reviewSaveDTO), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Delete Review By Id - OK")
    void deleteReviewById_thenReturnsOk() {
        ObjectId id = new ObjectId();
        Review reviewGiven = Review.builder().id(id).title("review to delete").build();
        when(reviewRepo.findById(id)).thenReturn(Optional.of(reviewGiven));
        Map<String, String> expectedRes = Map.of("message", String.format("Review with id: '%s' was successfully deleted", id));
        Map<String, String> response = reviewService.delete(id);
        assertNotNull(response);
        assertEquals(expectedRes.get("message"), response.get("message"));
    }

    @Test
    @DisplayName("Delete Review By Id - Throws Not Exists")
    void deleteReviewById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.delete(id), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Update Review By Id - OK")
    void updateReviewById_thenReturnsReview() {
        ObjectId id = new ObjectId();
        Review reviewGiven = Review.builder().id(id).title("review to update").build();
        ReviewDTO reviewDTO = ReviewDTO.builder().title("review updated").build();
        Review reviewSaved = Review.builder().id(id).title("review updated").build();

        when(reviewRepo.findById(id)).thenReturn(Optional.of(reviewGiven));
        when(reviewRepo.save(any(Review.class))).thenReturn(reviewSaved);

        Review review = reviewService.update(id, reviewDTO);
        assertNotNull(review);
    }

    @Test
    @DisplayName("Update Review By Id - Throws Not Exists")
    void updateReviewById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ReviewDTO reviewDTO = ReviewDTO.builder().title("review to update").build();
        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.update(id, reviewDTO), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Review By Id - OK")
    void patchReviewById_thenReturnsReview() throws JsonProcessingException, JsonPatchException {
        ObjectId id = new ObjectId();
        Review review = Review.builder().id(id).title("review to patch").build();
        when(reviewRepo.findById(id)).thenReturn(Optional.of(review));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("/title");

        when(objectMapper.treeToValue(null, Review.class)).thenReturn(review);
        when(reviewRepo.save(review)).thenReturn(review);

        Review reviewPatched = reviewService.patch(id, jsonPatch);
        assertNotNull(reviewPatched);
        verify(objectMapper, times(2)).convertValue(any(), eq(JsonNode.class));
        verify(objectMapper, times(1)).treeToValue(null, Review.class);
    }

    @Test
    @DisplayName("Patch Review By Id - Throws Not Exists")
    void patchReviewById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.patch(id, jsonPatch), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Review By Id - Throws Id Cannot Change")
    void patchReviewById_thenThrowsIdCannotChange() {
        ObjectId id = new ObjectId();
        Review review = Review.builder().id(id).title("review to patch").build();
        when(reviewRepo.findById(id)).thenReturn(Optional.of(review));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("/id");

        ErrorException thrown = assertThrows(ErrorException.class, () -> reviewService.patch(id, jsonPatch), "ErrorException was expected");
        assertEquals("ID key cannot be changed", thrown.getMessage());
    }
}