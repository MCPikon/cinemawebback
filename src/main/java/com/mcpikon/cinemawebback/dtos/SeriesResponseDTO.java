package com.mcpikon.cinemawebback.dtos;

import lombok.Builder;

@Builder
public record SeriesResponseDTO(String imdbId, String title, int numberOfSeasons, String releaseDate, String poster) { }