package com.mcpikon.cinemawebback.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ReviewSaveDTO(@NotBlank(message = "review title cannot be empty") String title,
                            @NotBlank(message = "review body cannot be empty") String body,
                            @NotBlank(message = "movie or series imdbId cannot be empty") String imdbId) { }