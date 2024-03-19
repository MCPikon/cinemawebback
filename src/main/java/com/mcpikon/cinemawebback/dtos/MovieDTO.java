package com.mcpikon.cinemawebback.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record MovieDTO(@NotBlank(message = "movie imdbId cannot be empty") String imdbId,
                       @NotBlank(message = "movie title cannot be empty") String title,
                       String overview, String duration,
                       @NotBlank(message = "movie director's name cannot be empty") String director,
                       String releaseDate, String trailerLink,
                       List<String> genres, String poster, String backdrop) { }