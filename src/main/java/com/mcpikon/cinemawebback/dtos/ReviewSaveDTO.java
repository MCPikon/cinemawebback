package com.mcpikon.cinemawebback.dtos;

import jakarta.validation.constraints.NotBlank;

public record ReviewSaveDTO(@NotBlank String title, @NotBlank String body, @NotBlank String imdbId) { }