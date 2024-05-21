package com.mcpikon.cinemawebback.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.mcpikon.cinemawebback.dtos.SeriesDTO;
import com.mcpikon.cinemawebback.dtos.SeriesResponseDTO;
import com.mcpikon.cinemawebback.exceptions.ErrorException;
import com.mcpikon.cinemawebback.models.Series;
import com.mcpikon.cinemawebback.services.SeriesService;
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

@WebMvcTest(controllers = SeriesController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SeriesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeriesService seriesService;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> seriesRes;
    private Series series;
    private SeriesDTO seriesDTO;
    private List<Map<String, String>> jsonPatchMap;

    @BeforeEach
    void init() {
        List<SeriesResponseDTO> seriesResDTOList = List.of(
                SeriesResponseDTO.builder().imdbId("tt12345").title("series 1 test").build(),
                SeriesResponseDTO.builder().imdbId("tt23456").title("series 2 test").build());
        seriesRes = Map.of("series", seriesResDTOList, "currentPage", 0, "totalItems", 2, "totalPages", 1);
        series = Series.builder().id(new ObjectId()).imdbId("tt12345").title("series test").creator("test").numberOfSeasons(2).seasonList(List.of(new Series.Season())).build();
        seriesDTO = SeriesDTO.builder().imdbId("tt12345").title("series test").creator("test").numberOfSeasons(2).seasonList(List.of(new Series.Season())).build();
        jsonPatchMap = List.of(Map.of("op", "replace", "path", "/title", "value", "series test"));
    }

    @Test
    @DisplayName("Find All Series - OK (200)")
    void findAllSeries_thenReturnOk() throws Exception {
        when(seriesService.findAll("series", 0, 10)).thenReturn(seriesRes);
        mockMvc.perform(get("/api/v1/series/findAll")
                        .param("title", "series")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.series[0].title").value("series 1 test"))
                .andDo(print());
    }

    @Test
    @DisplayName("Find All Series - No Content (204)")
    void findAllSeries_thenReturnNoContent() throws Exception {
        when(seriesService.findAll("test", 0, 10)).thenThrow(
                new ErrorException(EMPTY.getId(), EMPTY.getMessage(), EMPTY.getHttpStatus()));
        mockMvc.perform(get("/api/v1/series/findAll")
                        .param("title", "test")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @DisplayName("Find Series By Id - OK (200)")
    void findSeriesById_thenReturnOk() throws Exception {
        when(seriesService.findById(series.getId())).thenReturn(Optional.of(series));
        mockMvc.perform(get("/api/v1/series/findById/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(series.getTitle()))
                .andExpect(jsonPath("$.overview").value(series.getOverview()))
                .andDo(print());
    }

    @Test
    @DisplayName("Find Series By Id - Not Found (404)")
    void findSeriesById_thenReturnNotFound() throws Exception {
        when(seriesService.findById(series.getId())).thenThrow(
                new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(get("/api/v1/series/findById/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Find Series By ImdbId - OK (200)")
    void findSeriesByImdbId_thenReturnOk() throws Exception {
        when(seriesService.findByImdbId(series.getImdbId())).thenReturn(Optional.of(series));
        mockMvc.perform(get("/api/v1/series/findByImdbId/{imdbId}", series.getImdbId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(series.getTitle()))
                .andExpect(jsonPath("$.overview").value(series.getOverview()))
                .andDo(print());
    }

    @Test
    @DisplayName("Find Series By ImdbId - Not Found (404)")
    void findSeriesByImdbId_thenReturnNotFound() throws Exception {
        when(seriesService.findByImdbId(series.getImdbId())).thenThrow(
                new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(get("/api/v1/series/findByImdbId/{imdbId}", series.getImdbId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Save Series - OK (200)")
    void saveSeries_thenReturnOk() throws Exception {
        when(seriesService.save(seriesDTO)).thenReturn(series);
        mockMvc.perform(post("/api/v1/series/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seriesDTO)))
                .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    @DisplayName("Save Series - Bad Request (400)")
    void saveSeries_thenReturnBadRequest() throws Exception {
        when(seriesService.save(seriesDTO)).thenThrow(
                new ErrorException(ALREADY_EXISTS.getId(), ALREADY_EXISTS.getMessage(), ALREADY_EXISTS.getHttpStatus()));
        mockMvc.perform(post("/api/v1/series/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seriesDTO)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @DisplayName("Delete Series - OK (200)")
    void deleteSeries_thenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/series/delete/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @DisplayName("Delete Series - Not Found (404)")
    void deleteSeries_thenReturnNotFound() throws Exception {
        when(seriesService.delete(series.getId())).thenThrow(
                new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(delete("/api/v1/series/delete/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Update Series By Id - OK (200)")
    void updateSeriesById_thenReturnOk() throws Exception {
        when(seriesService.update(series.getId(), seriesDTO)).thenReturn(series);
        mockMvc.perform(put("/api/v1/series/update/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seriesDTO)))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @DisplayName("Update Series By Id - Not Found (404)")
    void updateSeriesById_thenReturnNotFound() throws Exception {
        when(seriesService.update(series.getId(), seriesDTO)).thenThrow(
                new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(put("/api/v1/series/update/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seriesDTO)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Update Series By Id - Bad Request (400)")
    void updateSeriesById_thenReturnBadRequest() throws Exception {
        when(seriesService.update(series.getId(), seriesDTO)).thenThrow(
                new ErrorException(IMDB_ID_ALREADY_IN_USE.getId(), IMDB_ID_ALREADY_IN_USE.getMessage(), IMDB_ID_ALREADY_IN_USE.getHttpStatus()));
        mockMvc.perform(put("/api/v1/series/update/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seriesDTO)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @DisplayName("Patch Series By Id - OK (200)")
    void patchSeriesById_thenReturnOk() throws Exception {
        when(seriesService.patch(eq(series.getId()), any(JsonPatch.class))).thenReturn(series);
        mockMvc.perform(patch("/api/v1/series/patch/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("series test"))
                .andDo(print());
    }

    @Test
    @DisplayName("Patch Series By Id - Not Found (404)")
    void patchSeriesById_thenReturnNotFound() throws Exception {
        when(seriesService.patch(eq(series.getId()), any(JsonPatch.class))).thenThrow(
                new ErrorException(NOT_EXISTS.getId(), NOT_EXISTS.getMessage(), NOT_EXISTS.getHttpStatus()));
        mockMvc.perform(patch("/api/v1/series/patch/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Patch Series By Id - Bad Request [ID cannot change] (400)")
    void patchSeriesById_thenReturnBadRequestId() throws Exception {
        when(seriesService.patch(eq(series.getId()), any(JsonPatch.class))).thenThrow(
                new ErrorException(ID_CANNOT_CHANGE.getId(), ID_CANNOT_CHANGE.getMessage(), ID_CANNOT_CHANGE.getHttpStatus()));
        MvcResult result = mockMvc.perform(patch("/api/v1/series/patch/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn();
        assertEquals(ID_CANNOT_CHANGE.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage());
    }

    @Test
    @DisplayName("Patch Series By Id - Bad Request [ImdbId already in use] (400)")
    void patchSeriesById_thenReturnBadRequestImdbId() throws Exception {
        when(seriesService.patch(eq(series.getId()), any(JsonPatch.class))).thenThrow(
                new ErrorException(IMDB_ID_ALREADY_IN_USE.getId(), IMDB_ID_ALREADY_IN_USE.getMessage(), IMDB_ID_ALREADY_IN_USE.getHttpStatus()));
        MvcResult result = mockMvc.perform(patch("/api/v1/series/patch/{id}", series.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonPatchMap)))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn();
        assertEquals(IMDB_ID_ALREADY_IN_USE.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage());
    }
}