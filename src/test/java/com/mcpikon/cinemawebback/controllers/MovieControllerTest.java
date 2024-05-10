package com.mcpikon.cinemawebback.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.mcpikon.cinemawebback.dtos.MovieDTO;
import com.mcpikon.cinemawebback.dtos.MovieResponseDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Movie;
import com.mcpikon.cinemawebback.services.MovieService;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.mcpikon.cinemawebback.exceptions.Errors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<MovieResponseDTO> movieResDTOList;
    private Movie movie;
    private MovieDTO movieDTO;
    private List<Map<String, String>> jsonPatchMap;

    @BeforeEach
    void init() {
        movieResDTOList = List.of(
                MovieResponseDTO.builder().imdbId("tt12345").title("movie 1 test").build(),
                MovieResponseDTO.builder().imdbId("tt23456").title("movie 2 test").build());
        movie = Movie.builder().id(new ObjectId()).imdbId("tt12345").title("movie test").director("test").overview("movie to test").build();
        movieDTO = MovieDTO.builder().imdbId("tt12345").title("movie test").director("test").overview("movie to test").build();
        jsonPatchMap = List.of(Map.of("op", "replace", "path", "/title", "value", "movie test"));
    }

    @Test
    @DisplayName("Find All Movies - OK (200)")
    void findAllMovies_thenReturnOk() throws Exception {
        when(movieService.findAll()).thenReturn(movieResDTOList);
        mockMvc.perform(get("/api/v1/movies/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("movie 1 test"))
                .andDo(print());
    }

    @Test
    @DisplayName("Find All Movies - No Content (204)")
    void findAllMovies_thenReturnNoContent() throws Exception {
        when(movieService.findAll())
                .thenThrow(new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus()));
        mockMvc.perform(get("/api/v1/movies/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @DisplayName("Find All Movies By Title - OK (200)")
    void findAllMoviesByTitle_thenReturnOk() throws Exception {
        when(movieService.findAllByTitle(movie.getTitle())).thenReturn(movieResDTOList);
        mockMvc.perform(get("/api/v1/movies/findAllByTitle/{title}", movie.getTitle())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("movie 1 test"))
                .andDo(print());
    }

    @Test
    @DisplayName("Find All Movies By Title - No Content (204)")
    void findAllMoviesByTitle_thenReturnNoContent() throws Exception {
        when(movieService.findAllByTitle(movie.getTitle()))
                .thenThrow(new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus()));
        mockMvc.perform(get("/api/v1/movies/findAllByTitle/{title}", movie.getTitle())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @DisplayName("Find Movie By Id - OK (200)")
    void findMovieById_thenReturnOk() throws Exception {
        when(movieService.findById(movie.getId())).thenReturn(Optional.of(movie));
        mockMvc.perform(get("/api/v1/movies/findById/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(movie.getTitle()))
                .andExpect(jsonPath("$.overview").value(movie.getOverview()))
                .andDo(print());
    }

    @Test
    @DisplayName("Find Movie By Id - Not Found (404)")
    void findMovieById_thenReturnNotFound() throws Exception {
        when(movieService.findById(movie.getId()))
                .thenThrow(new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(get("/api/v1/movies/findById/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Find Movie By ImdbId - OK (200)")
    void findMovieByImdbId_thenReturnOk() throws Exception {
        when(movieService.findByImdbId(movie.getImdbId())).thenReturn(Optional.of(movie));
        mockMvc.perform(get("/api/v1/movies/findByImdbId/{imdbId}", movie.getImdbId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(movie.getTitle()))
                .andExpect(jsonPath("$.overview").value(movie.getOverview()))
                .andDo(print());
    }

    @Test
    @DisplayName("Find Movie By ImdbId - Not Found (404)")
    void findMovieByImdbId_thenReturnNotFound() throws Exception {
        when(movieService.findByImdbId(movie.getImdbId()))
                .thenThrow(new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(get("/api/v1/movies/findByImdbId/{imdbId}", movie.getImdbId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Save Movie - Created (201)")
    void saveMovie_thenReturnCreated() throws Exception {
        when(movieService.save(movieDTO)).thenReturn(movie);
        mockMvc.perform(post("/api/v1/movies/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    @DisplayName("Save Movie - Bad Request (400)")
    void saveMovie_thenReturnBadRequest() throws Exception {
        when(movieService.save(movieDTO))
                .thenThrow(new ErrorException(ALREADY_EXISTS.getId(), ALREADY_EXISTS.getMessage(), ALREADY_EXISTS.getHttpStatus()));
        mockMvc.perform(post("/api/v1/movies/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @DisplayName("Delete Movie By Id - OK (200)")
    void deleteMovieById_thenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/delete/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @DisplayName("Delete Movie By Id - Not Found (404)")
    void deleteMovieById_thenReturnNotFound() throws Exception {
        when(movieService.delete(movie.getId())).thenThrow(
                new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(delete("/api/v1/movies/delete/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Update Movie By Id - OK (200)")
    void updateMovieById_thenReturnOk() throws Exception {
        when(movieService.update(movie.getId(), movieDTO)).thenReturn(movie);
        mockMvc.perform(put("/api/v1/movies/update/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("movie test"))
                .andDo(print());
    }

    @Test
    @DisplayName("Update Movie By Id - Not Found (404)")
    void updateMovieById_thenReturnNotFound() throws Exception {
        when(movieService.update(movie.getId(), movieDTO)).thenThrow(
                new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(put("/api/v1/movies/update/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Update Movie By Id - Bad Request (400)")
    void updateMovieById_thenReturnBadRequest() throws Exception {
        when(movieService.update(movie.getId(), movieDTO)).thenThrow(
                new ErrorException(IMDB_ID_ALREADY_IN_USE.getId(), IMDB_ID_ALREADY_IN_USE.getMessage(), IMDB_ID_ALREADY_IN_USE.getHttpStatus()));
        mockMvc.perform(put("/api/v1/movies/update/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @DisplayName("Patch Movie By Id - OK (200)")
    void patchMovieById_thenReturnOk() throws Exception {
        when(movieService.patch(eq(movie.getId()), any(JsonPatch.class))).thenReturn(movie);
        mockMvc.perform(patch("/api/v1/movies/patch/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("movie test"))
                .andDo(print());
    }

    @Test
    @DisplayName("Patch Movie By Id - Not Found (404)")
    void patchMovieById_thenReturnNotFound() throws Exception {
        when(movieService.patch(eq(movie.getId()), any(JsonPatch.class))).thenThrow(
                new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(patch("/api/v1/movies/patch/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Patch Movie By Id - Bad Request [ID cannot change] (400)")
    void patchMovieById_thenReturnBadRequestId() throws Exception {
        when(movieService.patch(eq(movie.getId()), any(JsonPatch.class))).thenThrow(
                new ErrorException(ID_CANNOT_CHANGE.getId(), ID_CANNOT_CHANGE.getMessage(), ID_CANNOT_CHANGE.getHttpStatus()));
        MvcResult result = mockMvc.perform(patch("/api/v1/movies/patch/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn();
        assertEquals(ID_CANNOT_CHANGE.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage());
    }

    @Test
    @DisplayName("Patch Movie By Id - Bad Request [ImdbId already in use] (400)")
    void patchMovieById_thenReturnBadRequestImdbId() throws Exception {
        when(movieService.patch(eq(movie.getId()), any(JsonPatch.class))).thenThrow(
                new ErrorException(IMDB_ID_ALREADY_IN_USE.getId(), IMDB_ID_ALREADY_IN_USE.getMessage(), IMDB_ID_ALREADY_IN_USE.getHttpStatus()));
        MvcResult result = mockMvc.perform(patch("/api/v1/movies/patch/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn();
        assertEquals(IMDB_ID_ALREADY_IN_USE.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage());
    }
}