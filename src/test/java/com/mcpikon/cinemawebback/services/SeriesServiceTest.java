package com.mcpikon.cinemawebback.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.mcpikon.cinemawebback.dtos.SeriesDTO;
import com.mcpikon.cinemawebback.dtos.SeriesResponseDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Series;
import com.mcpikon.cinemawebback.repositories.MovieRepository;
import com.mcpikon.cinemawebback.repositories.ReviewRepository;
import com.mcpikon.cinemawebback.repositories.SeriesRepository;
import com.mcpikon.cinemawebback.services.impl.SeriesServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeriesServiceTest {
    @Mock
    private SeriesRepository seriesRepo;

    @Mock
    private MovieRepository movieRepo;

    @Mock
    private ReviewRepository reviewRepo;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonPatch jsonPatch;

    @Mock
    private JsonNode jsonNode;

    @InjectMocks
    private SeriesServiceImpl seriesService;

    @Test
    @DisplayName("Find All Series - OK")
    void findAllSeries_thenReturnList() {
        List<Series> seriesList = List.of(
                Series.builder().title("series 1").build(),
                Series.builder().title("series 2").build());
        when(seriesRepo.findAll()).thenReturn(seriesList);
        List<SeriesResponseDTO> seriesFoundedList = seriesService.findAll();
        assertNotNull(seriesFoundedList);
        assertEquals(seriesList.size(), seriesFoundedList.size());
    }

    @Test
    @DisplayName("Find All Series - Throws Empty List")
    void findAllSeries_thenThrowsEmptyList() {
        when(seriesRepo.findAll()).thenReturn(new ArrayList<>());
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.findAll(), "ErrorException was expected");
        assertEquals("Empty List", thrown.getMessage());
    }

    @Test
    @DisplayName("Find Series By Id - OK")
    void findSeriesById_thenReturnsSeries() {
        ObjectId id = new ObjectId();
        Series seriesGiven = Series.builder().id(id).title("series test").overview("a series to find").build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(seriesGiven));
        Series seriesFounded = seriesService.findById(id).orElseThrow();
        assertNotNull(seriesFounded);
        assertEquals(seriesGiven.getId(), seriesFounded.getId());
    }

    @Test
    @DisplayName("Find Series By Id - Throws Not Exists")
    void findSeriesById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.findById(id), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Find Series By ImdbId - OK")
    void findSeriesByImdbId_thenReturnsSeries() {
        String imdbId = "tt12345";
        Series seriesGiven = Series.builder().imdbId(imdbId).title("series test").overview("a series to find").build();
        when(seriesRepo.findByImdbId(imdbId)).thenReturn(Optional.of(seriesGiven));
        Series seriesFounded = seriesService.findByImdbId(imdbId).orElseThrow();
        assertNotNull(seriesFounded);
        assertEquals(seriesGiven.getImdbId(), seriesFounded.getImdbId());
    }

    @Test
    @DisplayName("Find Series By ImdbId - Throws Not Exists")
    void findSeriesByImdbId_thenThrowsNotExists() {
        String imdbId = "tt54321";
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.findByImdbId(imdbId), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Save Series - OK")
    void saveSeries_thenReturnsSeries() {
        SeriesDTO seriesDTO = SeriesDTO.builder().title("Series Test").imdbId("tt12345").overview("A Series to save").build();
        Series series = Series.builder().title("Series Test").imdbId("tt12345").overview("A Series to save").build();

        when(seriesRepo.existsByImdbId(any(String.class))).thenReturn(false);
        when(movieRepo.existsByImdbId(any(String.class))).thenReturn(false);
        when(seriesRepo.insert(any(Series.class))).thenReturn(series);

        Series seriesSaved = seriesService.save(seriesDTO);
        assertNotNull(seriesSaved);
    }

    @Test
    @DisplayName("Save Series - Throws Already Exists")
    void saveSeries_thenThrowsNotExists() {
        when(seriesRepo.existsByImdbId(any(String.class))).thenReturn(true);
        SeriesDTO seriesDTO = SeriesDTO.builder().imdbId("tt12345").build();
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.save(seriesDTO), "ErrorException was expected");
        assertEquals("Entity already exists", thrown.getMessage());
    }

    // TODO: continuar añadiendo tests para los demás métodos
}