package com.mcpikon.cinemawebback.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ReviewDTO(@NotBlank(message = "review title cannot be empty") String title,
                        @Min(value = 0, message = "review rating cannot be less than zero")
                        @Max(value = 5, message = "review rating cannot be greater than five")
                        int rating,
                        @NotBlank(message = "review body cannot be empty") String body) { }