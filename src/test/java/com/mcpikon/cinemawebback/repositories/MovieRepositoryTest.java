package com.mcpikon.cinemawebback.repositories;

import com.mcpikon.cinemawebback.models.Movie;
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
class MovieRepositoryTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withExposedPorts(27017);

    @Autowired
    private MovieRepository movieRepo;

    @Test
    @Order(0)
    @DisplayName("Find All Movies - Empty List")
    void findAll_shouldGetEmpty() {
        assertEquals(0, movieRepo.findAll().size());
    }

    @Test
    @Order(1)
    @DisplayName("Find All Movies")
    void findAll_shouldNotEmpty_and_getTwoMovies() {
        movieRepo.saveAll(new ArrayList<>(Arrays.asList(
                Movie.builder().title("movie 1").build(),
                Movie.builder().title("movie 2").build())));
        List<Movie> movieList = movieRepo.findAll();
        assertFalse(movieList.isEmpty());
        assertEquals(2, movieList.size());
    }

    @Test
    @Order(2)
    @DisplayName("Find All Movies By Title - Empty List")
    void findAllByTitle_shouldGetEmpty() {
        assertEquals(0, movieRepo.findAllByTitle("search test").size());
    }

    @Test
    @Order(3)
    @DisplayName("Find All Movies By Title")
    void findAllByTitle_shouldNotEmpty_and_getTwoMovies() {
        List<Movie> movieList = movieRepo.findAllByTitle("movie");
        assertFalse(movieList.isEmpty());
        assertEquals(2, movieList.size());
    }

    @Test
    @Order(4)
    @DisplayName("Save a single Movie")
    void save_thenReturnSavedMovie() {
        Movie movie = Movie.builder().title("test").overview("a test movie").build();
        Movie savedMovie = movieRepo.insert(movie);
        assertNotNull(savedMovie);
    }

    @Test
    @Order(5)
    @DisplayName("Find By Id")
    void findById_shouldNotEmpty() {
        Movie movieSaved = movieRepo.insert(Movie.builder()
                .title("movie to find")
                .reviewIds(new ArrayList<>()).build());
        Movie movieFounded = movieRepo.findById(movieSaved.getId()).orElseThrow();
        assertEquals(movieSaved, movieFounded);
    }

    @Test
    @Order(6)
    @DisplayName("Find By Id - Throw Not Found")
    void findById_shouldThrowNotFound() {
        Optional<Movie> movieFounded = movieRepo.findById(new ObjectId());
        assertThrows(NoSuchElementException.class, movieFounded::orElseThrow);
    }

    @Test
    @Order(7)
    @DisplayName("Find By ImdbId")
    void findByImdbId_shouldNotEmpty() {
        Movie movieSaved = movieRepo.insert(Movie.builder()
                .title("movie to find")
                .imdbId("movie_imdbId_test")
                .reviewIds(new ArrayList<>()).build());
        Movie movieFounded = movieRepo.findByImdbId(movieSaved.getImdbId()).orElseThrow();
        assertEquals(movieSaved, movieFounded);
    }

    @Test
    @Order(8)
    @DisplayName("Find By ImdbId - Throw Not Found")
    void findByImdbId_shouldThrowNotFound() {
        Optional<Movie> movieFounded = movieRepo.findByImdbId("12345");
        assertThrows(NoSuchElementException.class, movieFounded::orElseThrow);
    }

    @Test
    @Order(9)
    @DisplayName("Delete a single Movie")
    void delete_shouldGetOk() {
        Movie movieToDelete = movieRepo.findByImdbId("movie_imdbId_test").orElseThrow();
        movieRepo.delete(movieToDelete);
        Optional<Movie> movieFounded = movieRepo.findByImdbId("movie_imdbId_test");
        assertThrows(NoSuchElementException.class, movieFounded::orElseThrow);
    }

    @Test
    @Order(10)
    @DisplayName("Exists by Id - True")
    void existsById_shouldGetTrue() {
        Movie movieSaved = movieRepo.insert(Movie.builder().title("movie exists test").build());
        assertTrue(movieRepo.existsById(movieSaved.getId()));
    }

    @Test
    @Order(11)
    @DisplayName("Exists by Id - False")
    void existsById_shouldGetFalse() {
        assertFalse(movieRepo.existsById(new ObjectId()));
    }

    @Test
    @Order(12)
    @DisplayName("Exists by ImdbId - True")
    void existsByImdbId_shouldGetTrue() {
        Movie movieSaved = movieRepo.insert(Movie.builder().title("movie exists test").imdbId("tt12345").build());
        assertTrue(movieRepo.existsByImdbId(movieSaved.getImdbId()));
    }

    @Test
    @Order(13)
    @DisplayName("Exists by ImdbId - False")
    void existsByImdbId_shouldGetFalse() {
        assertFalse(movieRepo.existsByImdbId("tt54321"));
    }
}