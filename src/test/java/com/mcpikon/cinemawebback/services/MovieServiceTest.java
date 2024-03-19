package com.mcpikon.cinemawebback.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.MovieDTO;
import com.mcpikon.cinemawebback.dtos.MovieResponseDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Movie;
import com.mcpikon.cinemawebback.models.Review;
import com.mcpikon.cinemawebback.repositories.MovieRepository;
import com.mcpikon.cinemawebback.repositories.ReviewRepository;
import com.mcpikon.cinemawebback.repositories.SeriesRepository;
import com.mcpikon.cinemawebback.services.impl.MovieServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {
    @Mock
    private MovieRepository movieRepo;

    @Mock
    private SeriesRepository seriesRepo;

    @Mock
    private ReviewRepository reviewRepo;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonPatch jsonPatch;

    @Mock
    private JsonNode jsonNode;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    @DisplayName("Find All Movies - OK")
    void findAllMovies_thenReturnList() {
        List<Movie> movieList = new ArrayList<>(Arrays.asList(
                Movie.builder().title("movie 1").build(),
                Movie.builder().title("movie 2").build()));
        when(movieRepo.findAll()).thenReturn(movieList);
        List<MovieResponseDTO> movieFoundedList = movieService.findAll();
        assertNotNull(movieFoundedList);
        assertEquals(movieList.size(), movieFoundedList.size());
    }

    @Test
    @DisplayName("Find All Movies - Throws Empty List")
    void findAllMovies_thenThrowsEmptyList() {
        when(movieRepo.findAll()).thenReturn(new ArrayList<>());
        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.findAll(), "ErrorException was expected");
        assertEquals("Empty List", thrown.getMessage());
    }

    @Test
    @DisplayName("Find Movie By Id - OK")
    void findMovieById_thenReturnsMovie() {
        ObjectId id = new ObjectId();
        Optional<Movie> movieGiven = Optional.of(Movie.builder().id(id).title("movie founded").build());
        when(movieRepo.findById(id)).thenReturn(movieGiven);
        Optional<Movie> movieFounded = movieService.findById(id);
        assertNotNull(movieFounded.orElseThrow());
        assertEquals(movieGiven.orElseThrow().getId(), movieFounded.orElseThrow().getId());
    }

    @Test
    @DisplayName("Find Movie By Id - Throws Not Exists")
    void findMovieById_thenThrowNotFound() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.findById(id), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Find Movie By ImdbId - OK")
    void findMovieByImdbId_thenReturnsMovie() {
        String imdbId = "tt12345";
        Optional<Movie> movieGiven = Optional.of(Movie.builder().imdbId(imdbId).title("movie founded").build());
        when(movieRepo.findByImdbId(imdbId)).thenReturn(movieGiven);
        Optional<Movie> movieFounded = movieService.findByImdbId(imdbId);
        assertNotNull(movieFounded.orElseThrow());
        assertEquals(movieGiven.orElseThrow().getImdbId(), movieFounded.orElseThrow().getImdbId());
    }

    @Test
    @DisplayName("Find Movie By ImdbId - Throws Not Exists")
    void findMovieByImdbId_thenThrowsNotExists() {
        String imdbId = "tt54321";
        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.findByImdbId(imdbId), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Save Movie - OK")
    void saveMovie_thenReturnsMovie() {
        MovieDTO movieDTO = MovieDTO.builder().title("Movie Test").imdbId("12345").overview("A movie to test").build();
        Movie movie = Movie.builder().title("Movie Test").imdbId("12345").overview("A movie to test").build();

        when(movieRepo.existsByImdbId(any(String.class))).thenReturn(false);
        when(seriesRepo.existsByImdbId(any(String.class))).thenReturn(false);
        when(movieRepo.insert(any(Movie.class))).thenReturn(movie);

        Movie movieSaved = movieService.save(movieDTO);
        assertNotNull(movieSaved);
    }

    @Test
    @DisplayName("Save Movie - Throws Already Exists")
    void saveMovie_thenThrowsAlreadyExists() {
        when(movieRepo.existsByImdbId(any(String.class))).thenReturn(true);
        MovieDTO movieDTO = MovieDTO.builder().imdbId("12345").build();
        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.save(movieDTO), "ErrorException was expected");
        assertEquals("Entity already exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Delete Movie By Id - OK")
    void deleteMovieById_thenReturnOk() {
        ObjectId id = new ObjectId();
        List<Review> reviewList = List.of(
                Review.builder().title("review 1").build(),
                Review.builder().title("review 2").build());
        Movie movieGiven = Movie.builder().id(id).title("movie to delete").reviewIds(reviewList).build();
        when(movieRepo.findById(id)).thenReturn(Optional.of(movieGiven));
        Map<String, String> expectedRes = Map.of("message", String.format("Movie with id: '%s' was successfully deleted", id));
        Map<String, String> response = movieService.delete(id);
        assertNotNull(response);
        assertEquals(expectedRes.get("message"), response.get("message"));
    }

    @Test
    @DisplayName("Delete Movie By Id - Throws Not Exists")
    void deleteMovieById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.delete(id), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Update Movie By Id - OK")
    void updateMovieById_thenReturnsMovie() {
        ObjectId id = new ObjectId();
        Movie movieGiven = Movie.builder().id(id).imdbId("tt12345").title("movie to update").build();
        MovieDTO movieDTO = MovieDTO.builder().imdbId("tt54321").title("movie updated").build();
        Movie movieUpdated = Movie.builder().id(id).imdbId("tt54321").title("movie updated").build();

        when(movieRepo.findById(id)).thenReturn(Optional.of(movieGiven));
        when(movieRepo.existsByImdbId(movieDTO.imdbId())).thenReturn(false);
        when(movieRepo.save(movieUpdated)).thenReturn(movieUpdated);

        Movie movie = movieService.update(id, movieDTO);
        assertNotNull(movie);
    }

    @Test
    @DisplayName("Update Movie by Id - Throws Not Exists")
    void updateMovieById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        MovieDTO movieDTO = MovieDTO.builder().title("movieDTO test").build();
        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.update(id, movieDTO), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Update Movie by Id - Throws ImdbId in use")
    void updateMovieById_thenThrowsImdbIdInUse() {
        ObjectId id = new ObjectId();
        Movie movieGiven = Movie.builder().id(id).imdbId("tt12345").title("movie to update").build();
        MovieDTO movieDTO = MovieDTO.builder().imdbId("tt54321").title("movie updated").build();

        when(movieRepo.findById(id)).thenReturn(Optional.of(movieGiven));
        when(movieRepo.existsByImdbId(movieDTO.imdbId())).thenReturn(true);

        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.update(id, movieDTO), "ErrorException was expected");
        assertEquals("The imdbId passed is already in use", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Movie by Id - OK")
    void patchMovieById_thenReturnsMovie() throws JsonProcessingException, JsonPatchException {
        ObjectId id = new ObjectId();
        Movie movie = Movie.builder().id(id).title("movie to patch").build();
        when(movieRepo.findById(id)).thenReturn(Optional.of(movie));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("/title");

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("value")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("movie patched");

        when(objectMapper.treeToValue(null, Movie.class)).thenReturn(movie);
        when(movieRepo.save(movie)).thenReturn(movie);

        Movie moviePatched = movieService.patch(id, jsonPatch);
        assertNotNull(moviePatched);
        verify(objectMapper, times(2)).convertValue(any(), eq(JsonNode.class));
        verify(objectMapper, times(1)).treeToValue(null, Movie.class);
    }

    @Test
    @DisplayName("Patch Movie by Id - Throws Not Exists")
    void patchMovieById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.patch(id, jsonPatch), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Movie by Id - Throws Id Cannot Change")
    void patchMovieById_thenThrowsIdCannotChange() {
        ObjectId id = new ObjectId();
        Movie movie = Movie.builder().id(id).title("movie to patch").build();
        when(movieRepo.findById(id)).thenReturn(Optional.of(movie));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        JsonNode innerJnPath = mock(JsonNode.class);
        JsonNode innerJnValue = mock(JsonNode.class);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(innerJnPath);
        when(innerJnPath.asText()).thenReturn("/id");
        when(jsonNode.get("value")).thenReturn(innerJnValue);
        when(innerJnValue.asText()).thenReturn("new id");

        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.patch(id, jsonPatch), "ErrorException was expected");
        assertEquals("ID key cannot be changed", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Movie by Id - Throws ImdbId in use")
    void patchMovieById_thenThrowsImdbIdInUse() {
        ObjectId id = new ObjectId();
        Movie movie = Movie.builder().id(id).title("movie to patch").imdbId("tt12345").build();
        when(movieRepo.findById(id)).thenReturn(Optional.of(movie));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        JsonNode innerJnPath = mock(JsonNode.class);
        JsonNode innerJnValue = mock(JsonNode.class);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(innerJnPath);
        when(innerJnPath.asText()).thenReturn("/imdbId");
        when(jsonNode.get("value")).thenReturn(innerJnValue);
        when(innerJnValue.asText()).thenReturn("tt54321");

        when(movieRepo.existsByImdbId("tt54321")).thenReturn(true);

        ErrorException thrown = assertThrows(ErrorException.class, () -> movieService.patch(id, jsonPatch), "ErrorException was expected");
        assertEquals("The imdbId passed is already in use", thrown.getMessage());
    }
}