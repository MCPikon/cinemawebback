package com.mcpikon.pelisWebBack.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MovieDTO(@NotBlank(message = "imdbId cannot be empty") String imdbId, @NotBlank String title,
                       String overview, String duration, String director,
                       String releaseDate, String trailerLink,
                       List<String> genres, String poster, String backdrop) { }