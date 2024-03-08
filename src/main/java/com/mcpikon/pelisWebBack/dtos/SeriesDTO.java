package com.mcpikon.pelisWebBack.dtos;

import com.mcpikon.pelisWebBack.models.Season;

import java.util.List;

public record SeriesDTO(String imdbId, String title, String overview, int numberOfSeasons, String creator, String releaseDate,
                        String trailerLink, List<String> genres, List<Season> seasonList, String poster, String backdrop) {
}
