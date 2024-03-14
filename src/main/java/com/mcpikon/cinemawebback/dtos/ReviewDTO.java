package com.mcpikon.cinemawebback.dtos;

import jakarta.validation.constraints.NotBlank;

public record ReviewDTO(@NotBlank String title, @NotBlank String body) { }