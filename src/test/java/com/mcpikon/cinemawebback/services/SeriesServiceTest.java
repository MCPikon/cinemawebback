package com.mcpikon.cinemawebback.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mcpikon.cinemawebback.dtos.SeriesDTO;
import com.mcpikon.cinemawebback.dtos.SeriesResponseDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Review;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
        List<SeriesResponseDTO> seriesResDTOList = Arrays.asList(
                SeriesResponseDTO.builder().title("series 1").build(),
                SeriesResponseDTO.builder().title("series 2").build());
        List<Series> seriesList = Arrays.asList(
                Series.builder().title("series 1").build(),
                Series.builder().title("series 2").build());
        Pageable paging = PageRequest.of(0, 10);
        Page<Series> seriesPage = new PageImpl<>(seriesList, paging, seriesList.size());
        when(seriesRepo.findAll(paging)).thenReturn(seriesPage);
        Map<String, Object> seriesResMap = seriesService.findAll(null, 0, 10);
        assertNotNull(seriesResMap);
        assertEquals(seriesResDTOList, seriesResMap.get("series"));
    }

    @Test
    @DisplayName("Find All Series By Title - OK")
    void findAllSeriesByTitle_thenReturnList() {
        List<SeriesResponseDTO> seriesResDTOList = Arrays.asList(
                SeriesResponseDTO.builder().title("series 1").build(),
                SeriesResponseDTO.builder().title("series 2").build());
        List<Series> seriesList = Arrays.asList(
                Series.builder().title("series 1").build(),
                Series.builder().title("series 2").build());
        Pageable paging = PageRequest.of(0, 10);
        Page<Series> seriesPage = new PageImpl<>(seriesList, paging, seriesList.size());
        when(seriesRepo.findAllByTitle("series", paging)).thenReturn(seriesPage);
        Map<String, Object> seriesResMap = seriesService.findAll("series", 0, 10);
        assertNotNull(seriesResMap);
        assertEquals(seriesResDTOList, seriesResMap.get("series"));
    }

    @Test
    @DisplayName("Find All Series - Throws Empty List")
    void findAllSeries_thenThrowsEmptyList() {
        Pageable paging = PageRequest.of(0, 1);
        when(seriesRepo.findAll(paging)).thenReturn(new PageImpl<>(new ArrayList<>(), paging, 0));
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.findAll(null, -1, -1), "ErrorException was expected");
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
    @DisplayName("Save Series - Throws Series Already Exists")
    void saveSeries_thenThrowsSeriesNotExists() {
        when(seriesRepo.existsByImdbId(any(String.class))).thenReturn(true);
        SeriesDTO seriesDTO = SeriesDTO.builder().imdbId("tt12345").build();
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.save(seriesDTO), "ErrorException was expected");
        assertEquals("Entity already exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Save Series - Throws Movie Already Exists")
    void saveSeries_thenThrowsMovieNotExists() {
        when(movieRepo.existsByImdbId(any(String.class))).thenReturn(true);
        SeriesDTO seriesDTO = SeriesDTO.builder().imdbId("tt12345").build();
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.save(seriesDTO), "ErrorException was expected");
        assertEquals("Entity already exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Delete Series By Id - OK")
    void deleteSeriesById_thenReturnsOk() {
        ObjectId id = new ObjectId();
        List<Review> reviewList = List.of(
                Review.builder().title("review 1").build(),
                Review.builder().title("review 2").build());
        Series seriesGiven = Series.builder().id(id).title("series to delete").reviewIds(reviewList).build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(seriesGiven));
        Map<String, String> expectedRes = Map.of("message", String.format("Series with id: '%s' was successfully deleted", id));
        Map<String, String> response = seriesService.delete(id);
        assertNotNull(response);
        assertEquals(expectedRes.get("message"), response.get("message"));
    }

    @Test
    @DisplayName("Delete Series By Id - Throws Not Exists")
    void deleteSeriesById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.delete(id), "ErrorException was expected");
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Update Series By Id - OK (Exists By ImdbId)")
    void updateSeriesById_whenExistsByImdbId_thenReturnsSeries() {
        ObjectId id = new ObjectId();
        Series seriesGiven = Series.builder().id(id).imdbId("tt12345").title("series to update").build();
        SeriesDTO seriesDTO = SeriesDTO.builder().imdbId("tt54321").title("series updated").build();
        Series seriesUpdated = Series.builder().id(id).imdbId("tt54321").title("series updated").build();

        when(seriesRepo.findById(id)).thenReturn(Optional.of(seriesGiven));
        when(movieRepo.existsByImdbId(seriesDTO.imdbId())).thenReturn(false);
        when(seriesRepo.save(seriesUpdated)).thenReturn(seriesUpdated);

        Series series = seriesService.update(id, seriesDTO);
        assertNotNull(series);
    }

    @Test
    @DisplayName("Update Series By Id - OK (Equal ImdbId)")
    void updateSeriesById_whenEqualImdbId_thenReturnsSeries() {
        ObjectId id = new ObjectId();
        Series seriesGiven = Series.builder().id(id).imdbId("tt12345").title("series to update").build();
        SeriesDTO seriesDTO = SeriesDTO.builder().imdbId("tt12345").title("series updated").build();
        Series seriesUpdated = Series.builder().id(id).imdbId("tt12345").title("series updated").build();

        when(seriesRepo.findById(id)).thenReturn(Optional.of(seriesGiven));
        when(movieRepo.existsByImdbId(seriesDTO.imdbId())).thenReturn(true);
        when(seriesRepo.save(seriesUpdated)).thenReturn(seriesUpdated);

        Series series = seriesService.update(id, seriesDTO);
        assertNotNull(series);
    }

    @Test
    @DisplayName("Update Series By Id - Throws Not Exists")
    void updateSeriesById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        SeriesDTO seriesDTO = SeriesDTO.builder().title("seriesDTO test").build();
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.update(id, seriesDTO));
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Update Series By Id - Throws ImdbId in use by Series")
    void updateSeriesById_thenThrowsImdbIdInUseBySeries() {
        ObjectId id = new ObjectId();
        Series seriesGiven = Series.builder().id(id).imdbId("tt12345").title("series to update").build();
        SeriesDTO seriesDTO = SeriesDTO.builder().imdbId("tt54321").title("series updated").build();

        when(seriesRepo.findById(id)).thenReturn(Optional.of(seriesGiven));
        when(seriesRepo.existsByImdbId(seriesDTO.imdbId())).thenReturn(true);

        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.update(id, seriesDTO));
        assertEquals("The imdbId passed is already in use", thrown.getMessage());
    }

    @Test
    @DisplayName("Update Series By Id - Throws ImdbId in use by Movie")
    void updateSeriesById_thenThrowsImdbIdInUseByMovie() {
        ObjectId id = new ObjectId();
        Series seriesGiven = Series.builder().id(id).imdbId("tt12345").title("series to update").build();
        SeriesDTO seriesDTO = SeriesDTO.builder().imdbId("tt54321").title("series updated").build();

        when(seriesRepo.findById(id)).thenReturn(Optional.of(seriesGiven));
        when(movieRepo.existsByImdbId(seriesDTO.imdbId())).thenReturn(true);

        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.update(id, seriesDTO));
        assertEquals("The imdbId passed is already in use", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Series By Id - OK")
    void patchSeriesById_thenReturnsSeries() throws JsonProcessingException, JsonPatchException {
        ObjectId id = new ObjectId();
        Series series = Series.builder().id(id).title("series to patch").build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(series));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("/title");

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("value")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("series patched");

        when(objectMapper.treeToValue(null, Series.class)).thenReturn(series);
        when(seriesRepo.save(series)).thenReturn(series);

        Series seriesPatched = seriesService.patch(id, jsonPatch);
        assertNotNull(seriesPatched);
        verify(objectMapper, times(2)).convertValue(any(), eq(JsonNode.class));
        verify(objectMapper, times(1)).treeToValue(null, Series.class);
    }

    @Test
    @DisplayName("Patch Series By Id - OK (Not Exists by Series ImdbId)")
    void patchSeriesById_whenNotExistsBySeriesImdbId_thenReturnsSeries() throws JsonProcessingException, JsonPatchException {
        ObjectId id = new ObjectId();
        Series series = Series.builder().id(id).imdbId("tt12345").title("series to patch").build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(series));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        JsonNode innerJnPath = mock(JsonNode.class);
        JsonNode innerJnValue = mock(JsonNode.class);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(innerJnPath);
        when(innerJnPath.asText()).thenReturn("/imdbId");
        when(jsonNode.get("value")).thenReturn(innerJnValue);
        when(innerJnValue.asText()).thenReturn("tt54321");

        when(seriesRepo.existsByImdbId("tt54321")).thenReturn(false);
        when(objectMapper.treeToValue(null, Series.class)).thenReturn(series);
        when(seriesRepo.save(series)).thenReturn(series);

        Series seriesPatched = seriesService.patch(id, jsonPatch);
        assertNotNull(seriesPatched);
        verify(objectMapper, times(2)).convertValue(any(), eq(JsonNode.class));
        verify(objectMapper, times(1)).treeToValue(null, Series.class);
    }

    @Test
    @DisplayName("Patch Series By Id - OK (Not Exists by Movie ImdbId)")
    void patchSeriesById_whenNotExistsByMovieImdbId_thenReturnsSeries() throws JsonProcessingException, JsonPatchException {
        ObjectId id = new ObjectId();
        Series series = Series.builder().id(id).imdbId("tt12345").title("series to patch").build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(series));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        JsonNode innerJnPath = mock(JsonNode.class);
        JsonNode innerJnValue = mock(JsonNode.class);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(innerJnPath);
        when(innerJnPath.asText()).thenReturn("/imdbId");
        when(jsonNode.get("value")).thenReturn(innerJnValue);
        when(innerJnValue.asText()).thenReturn("tt54321");

        when(movieRepo.existsByImdbId("tt54321")).thenReturn(false);
        when(objectMapper.treeToValue(null, Series.class)).thenReturn(series);
        when(seriesRepo.save(series)).thenReturn(series);

        Series seriesPatched = seriesService.patch(id, jsonPatch);
        assertNotNull(seriesPatched);
        verify(objectMapper, times(2)).convertValue(any(), eq(JsonNode.class));
        verify(objectMapper, times(1)).treeToValue(null, Series.class);
    }

    @Test
    @DisplayName("Patch Series By Id - OK (Equal ImdbId)")
    void patchSeriesById_whenEqualImdbId_thenReturnsSeries() throws JsonProcessingException, JsonPatchException {
        ObjectId id = new ObjectId();
        Series series = Series.builder().id(id).imdbId("tt54321").title("series to patch").build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(series));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        JsonNode innerJnPath = mock(JsonNode.class);
        JsonNode innerJnValue = mock(JsonNode.class);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(innerJnPath);
        when(innerJnPath.asText()).thenReturn("/imdbId");
        when(jsonNode.get("value")).thenReturn(innerJnValue);
        when(innerJnValue.asText()).thenReturn("tt54321");

        when(seriesRepo.existsByImdbId("tt54321")).thenReturn(true);
        when(objectMapper.treeToValue(null, Series.class)).thenReturn(series);
        when(seriesRepo.save(series)).thenReturn(series);

        Series seriesPatched = seriesService.patch(id, jsonPatch);
        assertNotNull(seriesPatched);
        verify(objectMapper, times(2)).convertValue(any(), eq(JsonNode.class));
        verify(objectMapper, times(1)).treeToValue(null, Series.class);
    }

    @Test
    @DisplayName("Patch Series By Id - Throws Not Exists")
    void patchSeriesById_thenThrowsNotExists() {
        ObjectId id = new ObjectId();
        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.patch(id, jsonPatch));
        assertEquals("Entity doesn't exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Series by Id - Throws Id Cannot Change")
    void patchSeriesById_thenThrowsIdCannotChange() {
        ObjectId id = new ObjectId();
        Series series = Series.builder().id(id).title("series to patch").build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(series));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        JsonNode innerJnPath = mock(JsonNode.class);
        JsonNode innerJnValue = mock(JsonNode.class);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(innerJnPath);
        when(innerJnPath.asText()).thenReturn("/id");
        when(jsonNode.get("value")).thenReturn(innerJnValue);
        when(innerJnValue.asText()).thenReturn("new id");

        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.patch(id, jsonPatch), "ErrorException was expected");
        assertEquals("ID key cannot be changed", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Series by Id - Throws ImdbId in use by Series")
    void patchSeriesById_thenThrowsImdbIdInUseBySeries() {
        ObjectId id = new ObjectId();
        Series series = Series.builder().id(id).title("series to patch").imdbId("tt12345").build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(series));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        JsonNode innerJnPath = mock(JsonNode.class);
        JsonNode innerJnValue = mock(JsonNode.class);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(innerJnPath);
        when(innerJnPath.asText()).thenReturn("/imdbId");
        when(jsonNode.get("value")).thenReturn(innerJnValue);
        when(innerJnValue.asText()).thenReturn("tt54321");

        when(seriesRepo.existsByImdbId("tt54321")).thenReturn(true);

        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.patch(id, jsonPatch), "ErrorException was expected");
        assertEquals("The imdbId passed is already in use", thrown.getMessage());
    }

    @Test
    @DisplayName("Patch Series by Id - Throws ImdbId in use by Movie")
    void patchSeriesById_thenThrowsImdbIdInUseByMovie() {
        ObjectId id = new ObjectId();
        Series series = Series.builder().id(id).title("series to patch").imdbId("tt12345").build();
        when(seriesRepo.findById(id)).thenReturn(Optional.of(series));

        when(objectMapper.convertValue(jsonPatch, JsonNode.class)).thenReturn(jsonNode);

        JsonNode innerJnPath = mock(JsonNode.class);
        JsonNode innerJnValue = mock(JsonNode.class);

        when(jsonNode.get(0)).thenReturn(jsonNode);
        when(jsonNode.get("path")).thenReturn(innerJnPath);
        when(innerJnPath.asText()).thenReturn("/imdbId");
        when(jsonNode.get("value")).thenReturn(innerJnValue);
        when(innerJnValue.asText()).thenReturn("tt54321");

        when(movieRepo.existsByImdbId("tt54321")).thenReturn(true);

        ErrorException thrown = assertThrows(ErrorException.class, () -> seriesService.patch(id, jsonPatch), "ErrorException was expected");
        assertEquals("The imdbId passed is already in use", thrown.getMessage());
    }
}