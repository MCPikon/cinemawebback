package com.mcpikon.cinemawebback.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpikon.cinemawebback.dtos.ReviewDTO;
import com.mcpikon.cinemawebback.dtos.ReviewSaveDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Review;
import com.mcpikon.cinemawebback.services.ReviewService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.mcpikon.cinemawebback.exceptions.Errors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Review> reviewList;
    private String imdbId;
    private Review review;
    private ReviewDTO reviewDTO;
    private ReviewSaveDTO reviewSaveDTO;
    private List<Map<String, String>> jsonPatchMap;

    @BeforeEach
    void init() {
        reviewList = List.of(
                Review.builder().title("review 1").body("review 1 test").build(),
                Review.builder().title("review 2").body("review 2 test").build());
        imdbId = "tt54321";
        review = Review.builder().id(new ObjectId()).title("review test").body("review to test").updatedAt(LocalDateTime.now()).createdAt(LocalDateTime.now()).build();
        reviewDTO = ReviewDTO.builder().title("review test").body("review to test").build();
        reviewSaveDTO = ReviewSaveDTO.builder().title("review test").body("review to test").imdbId("tt12345").build();
        jsonPatchMap = List.of(Map.of("op", "replace", "path", "/title", "value", "review test"));
    }

    @Test
    @DisplayName("Find All Reviews - OK (200)")
    void findAllReviews_thenReturnOk() throws Exception {
        when(reviewService.findAll()).thenReturn(reviewList);
        mockMvc.perform(get("/api/v1/reviews/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("review 1"))
                .andDo(print());
    }

    @Test
    @DisplayName("Find All Reviews - No Content (204)")
    void findAllReviews_thenReturnNoContent() throws Exception {
        when(reviewService.findAll())
                .thenThrow(new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus()));
        mockMvc.perform(get("/api/v1/reviews/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @DisplayName("Find All Reviews By ImdbId - OK (200)")
    void findAllReviewsByImdbId_thenReturnOk() throws Exception {
        when(reviewService.findAllByImdbId(imdbId)).thenReturn(reviewList);
        mockMvc.perform(get("/api/v1/reviews/findAllByImdbId/{imdbId}", imdbId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("review 1"))
                .andDo(print());
    }

    @Test
    @DisplayName("Find All Reviews By ImdbId - Not Found (404)")
    void findAllReviewsByImdbId_thenReturnNotFound() throws Exception {
        when(reviewService.findAllByImdbId(imdbId))
                .thenThrow(new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(get("/api/v1/reviews/findAllByImdbId/{imdbId}", imdbId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Find All Reviews By ImdbId - No Content (204)")
    void findAllReviewsByImdbId_thenReturnNoContent() throws Exception {
        when(reviewService.findAllByImdbId(imdbId))
                .thenThrow(new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus()));
        mockMvc.perform(get("/api/v1/reviews/findAllByImdbId/{imdbId}", imdbId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    // TODO: continuar añadiendo más tests para cubrir todos los métodos
}