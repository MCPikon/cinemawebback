package com.mcpikon.cinemawebback.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.mcpikon.cinemawebback.exceptions.Errors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    @DisplayName("Find Review By Id - OK (200)")
    void findReviewById_thenReturnOk() throws Exception {
        when(reviewService.findById(review.getId())).thenReturn(Optional.of(review));
        mockMvc.perform(get("/api/v1/reviews/findById/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(review.getTitle()))
                .andExpect(jsonPath("$.body").value(review.getBody()))
                .andDo(print());
    }

    @Test
    @DisplayName("Find Review By Id - Not Found (404)")
    void findReviewById_thenReturnNotFound() throws Exception {
        when(reviewService.findById(review.getId()))
                .thenThrow(new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(get("/api/v1/reviews/findById/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Save Review - OK (200)")
    void saveReview_thenReturnOk() throws Exception {
        when(reviewService.save(reviewSaveDTO)).thenReturn(review);
        mockMvc.perform(post("/api/v1/reviews/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSaveDTO)))
                .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    @DisplayName("Save Review - Not Found (404)")
    void saveReview_thenReturnNotFound() throws Exception {
        when(reviewService.save(reviewSaveDTO))
                .thenThrow(new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(post("/api/v1/reviews/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSaveDTO)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Delete Review By Id - OK (200)")
    void deleteReviewById_thenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/reviews/delete/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @DisplayName("Delete Review By Id - Not Found (404)")
    void deleteReviewById_thenReturnNotFound() throws Exception {
        when(reviewService.delete(review.getId()))
                .thenThrow(new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(delete("/api/v1/reviews/delete/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Update Review By Id - OK (200)")
    void updateReviewById_thenReturnOk() throws Exception {
        when(reviewService.update(review.getId(), reviewDTO)).thenReturn(review);
        mockMvc.perform(put("/api/v1/reviews/update/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("review test"))
                .andDo(print());
    }

    @Test
    @DisplayName("Update Review By Id - Not Found (404)")
    void updateReviewById_thenReturnNotFound() throws Exception {
        when(reviewService.update(review.getId(), reviewDTO))
                .thenThrow(new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(put("/api/v1/reviews/update/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Patch Review By Id - OK (200)")
    void patchReviewById_thenReturnOk() throws Exception {
        when(reviewService.patch(eq(review.getId()), any(JsonPatch.class))).thenReturn(review);
        mockMvc.perform(patch("/api/v1/reviews/patch/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("review test"))
                .andDo(print());
    }

    @Test
    @DisplayName("Patch Review By Id - Not Found (404)")
    void patchReviewById_thenReturnNotFound() throws Exception {
        when(reviewService.patch(eq(review.getId()), any(JsonPatch.class)))
                .thenThrow(new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(patch("/api/v1/reviews/patch/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Patch Review By Id - Bad Request [ID cannot change] (400)")
    void patchReviewById_thenReturnBadRequest() throws Exception {
        when(reviewService.patch(eq(review.getId()), any(JsonPatch.class)))
                .thenThrow(new ErrorException(ID_CANNOT_CHANGE.getId(), ID_CANNOT_CHANGE.getMessage(), ID_CANNOT_CHANGE.getHttpStatus()));
        MvcResult result = mockMvc.perform(patch("/api/v1/reviews/patch/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn();
        assertEquals(ID_CANNOT_CHANGE.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage());
    }
}