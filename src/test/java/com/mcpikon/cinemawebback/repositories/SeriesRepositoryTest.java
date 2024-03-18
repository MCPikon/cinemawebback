package com.mcpikon.cinemawebback.repositories;

import com.mcpikon.cinemawebback.models.Series;
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
class SeriesRepositoryTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withExposedPorts(27017);

    @Autowired
    private SeriesRepository seriesRepo;

    @Test
    @Order(0)
    @DisplayName("Find All Series - Empty List")
    void findAll_shouldGetEmpty() {
        assertEquals(0, seriesRepo.findAll().size());
    }

    @Test
    @Order(1)
    @DisplayName("Find All Series")
    void findAll_shouldNotEmpty_and_getTwoSeries() {
        seriesRepo.saveAll(new ArrayList<>(Arrays.asList(
                Series.builder().title("series 1").build(),
                Series.builder().title("series 2").build())));
        List<Series> seriesList = seriesRepo.findAll();
        assertFalse(seriesList.isEmpty());
        assertEquals(2, seriesList.size());
    }

    @Test
    @Order(3)
    @DisplayName("Save a Single Series")
    void save_thenReturnSavedSeries() {
        Series series = Series.builder().title("test").overview("a series test").build();
        Series savedSeries = seriesRepo.save(series);
        assertNotNull(savedSeries);
    }

    @Test
    @Order(3)
    @DisplayName("Find By Id")
    void findById_shouldNotEmpty() {
        Series seriesSaved = seriesRepo.insert(Series.builder()
                .title("series to find")
                .reviewIds(new ArrayList<>()).build());
        Series seriesFounded = seriesRepo.findById(seriesSaved.getId()).orElseThrow();
        assertEquals(seriesSaved, seriesFounded);
    }

    @Test
    @Order(4)
    @DisplayName("Find By Id - Throw Not Found")
    void findById_shouldThrowNotFound() {
        Optional<Series> seriesFounded = seriesRepo.findById(new ObjectId());
        assertThrows(NoSuchElementException.class, seriesFounded::orElseThrow);
    }

    @Test
    @Order(5)
    @DisplayName("Find By ImdbId")
    void findByImdbId_shouldNotEmpty() {
        Series seriesSaved = seriesRepo.insert(Series.builder()
                .title("series to find")
                .imdbId("series_imdbId_test")
                .reviewIds(new ArrayList<>()).build());
        Series seriesFounded = seriesRepo.findByImdbId(seriesSaved.getImdbId()).orElseThrow();
        assertEquals(seriesSaved, seriesFounded);
    }

    @Test
    @Order(6)
    @DisplayName("Find By ImdbId - Throw Not Found")
    void findByImdbId_shouldThrowNotFound() {
        Optional<Series> seriesFounded = seriesRepo.findByImdbId("12345");
        assertThrows(NoSuchElementException.class, seriesFounded::orElseThrow);
    }

    @Test
    @Order(7)
    @DisplayName("Delete a single Series")
    void delete_shouldGetOk() {
        Series seriesToDelete = seriesRepo.findByImdbId("series_imdbId_test").orElseThrow();
        seriesRepo.delete(seriesToDelete);
        Optional<Series> seriesFounded = seriesRepo.findByImdbId("series_imdbId_test");
        assertThrows(NoSuchElementException.class, seriesFounded::orElseThrow);
    }

    @Test
    @Order(8)
    @DisplayName("Exists by Id - True")
    void existsById_shouldGetTrue() {
        Series seriesSaved = seriesRepo.insert(Series.builder().title("series exists test").build());
        assertTrue(seriesRepo.existsById(seriesSaved.getId()));
    }

    @Test
    @Order(9)
    @DisplayName("Exists by Id - False")
    void existsById_shouldGetFalse() {
        assertFalse(seriesRepo.existsById(new ObjectId()));
    }

    @Test
    @Order(10)
    @DisplayName("Exists by ImdbId - True")
    void existsByImdbId_shouldGetTrue() {
        Series seriesSaved = seriesRepo.insert(Series.builder().title("series exists test").imdbId("tt67890").build());
        assertTrue(seriesRepo.existsByImdbId(seriesSaved.getImdbId()));
    }

    @Test
    @Order(11)
    @DisplayName("Exists by ImdbId - False")
    void existsByImdbId_shouldGetFalse() {
        assertFalse(seriesRepo.existsByImdbId("tt09876"));
    }
}