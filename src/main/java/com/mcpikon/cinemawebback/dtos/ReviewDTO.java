package com.mcpikon.cinemawebback.dtos;

import jakarta.validation.constraints.NotBlank;

public record ReviewDTO(@NotBlank(message = "review title cannot be empty") String title, @NotBlank(message = "review body cannot be empty") String body) { }