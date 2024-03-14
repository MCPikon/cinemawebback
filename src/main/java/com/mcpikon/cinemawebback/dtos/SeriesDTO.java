package com.mcpikon.cinemawebback.dtos;

import com.mcpikon.cinemawebback.models.Series;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SeriesDTO(@NotBlank(message = "series imdbId cannot be empty") String imdbId,
                        @NotBlank String title, String overview,
                        @NotNull(message = "number of seasons cannot be null")
                        @Min(value = 1, message = "number of seasons cannot be minor than 1")
                        int numberOfSeasons,
                        @NotBlank(message = "series creator's name cannot be empty") String creator,
                        String releaseDate, String trailerLink, List<String> genres,
                        @NotEmpty(message = "season list cannot be empty") List<Series.Season> seasonList,
                        String poster, String backdrop) { }