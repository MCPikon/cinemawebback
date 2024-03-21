package com.mcpikon.cinemawebback.dtos;

import lombok.Builder;

@Builder
public record MovieResponseDTO(String imdbId, String title, String duration, String releaseDate, String poster) { }