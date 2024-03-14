package com.mcpikon.cinemawebback.dtos;

import com.mcpikon.cinemawebback.models.Series;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SeriesDTO(@NotBlank String imdbId, @NotBlank String title, String overview,
                        @NotNull @Min(1) int numberOfSeasons, String creator, String releaseDate,
                        String trailerLink, List<String> genres, @NotEmpty List<Series.Season> seasonList,
                        String poster, String backdrop) { }