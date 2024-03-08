package com.mcpikon.pelisWebBack.dtos;

import java.util.List;

public record MovieDTO(String imdbId, String title, String overview, String duration, String director,
                       String releaseDate, String trailerLink, List<String> genres, String poster, String backdrop) {
}
