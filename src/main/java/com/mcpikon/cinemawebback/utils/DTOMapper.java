package com.mcpikon.cinemawebback.utils;

import com.mcpikon.cinemawebback.dtos.*;
import com.mcpikon.cinemawebback.models.Movie;
import com.mcpikon.cinemawebback.models.Review;
import com.mcpikon.cinemawebback.models.Series;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DTOMapper {

    private DTOMapper() {}

    // Movies DTOs Converters
    public static Movie dtoToMovie(MovieDTO movieDTO) {
        return Movie.builder()
                .imdbId(movieDTO.imdbId())
                .title(movieDTO.title())
                .overview(movieDTO.overview())
                .duration(movieDTO.duration())
                .director(movieDTO.director())
                .releaseDate(movieDTO.releaseDate())
                .trailerLink(movieDTO.trailerLink())
                .genres(movieDTO.genres())
                .poster(movieDTO.poster())
                .backdrop(movieDTO.backdrop())
                .reviewIds(new ArrayList<>()).build();
    }

    public static Movie dtoToMovieUpdate(Movie movie, MovieDTO movieDTO) {
        return Movie.builder()
                .id(movie.getId())
                .imdbId(movieDTO.imdbId())
                .title(movieDTO.title())
                .overview(movieDTO.overview())
                .duration(movieDTO.duration())
                .director(movieDTO.director())
                .releaseDate(movieDTO.releaseDate())
                .trailerLink(movieDTO.trailerLink())
                .genres(movieDTO.genres())
                .poster(movieDTO.poster())
                .backdrop(movieDTO.backdrop())
                .reviewIds(movie.getReviewIds()).build();
    }

    public static MovieResponseDTO movieToResponseDTO(Movie movie) {
        return new MovieResponseDTO(movie.getImdbId(), movie.getTitle(),
                movie.getDuration(), movie.getReleaseDate(), movie.getPoster());
    }

    // Series DTOs Converters
    public static Series dtoToSeries(SeriesDTO seriesDTO) {
        return Series.builder()
                .imdbId(seriesDTO.imdbId())
                .title(seriesDTO.title())
                .overview(seriesDTO.overview())
                .numberOfSeasons(seriesDTO.numberOfSeasons())
                .creator(seriesDTO.creator())
                .releaseDate(seriesDTO.releaseDate())
                .trailerLink(seriesDTO.trailerLink())
                .genres(seriesDTO.genres())
                .seasonList(seriesDTO.seasonList())
                .poster(seriesDTO.poster())
                .backdrop(seriesDTO.backdrop())
                .reviewIds(new ArrayList<>()).build();
    }

    public static Series dtoToSeriesUpdate(Series series, SeriesDTO seriesDTO) {
        return Series.builder()
                .id(series.getId())
                .imdbId(seriesDTO.imdbId())
                .title(seriesDTO.title())
                .overview(seriesDTO.overview())
                .numberOfSeasons(seriesDTO.numberOfSeasons())
                .creator(seriesDTO.creator())
                .releaseDate(seriesDTO.releaseDate())
                .trailerLink(seriesDTO.trailerLink())
                .genres(seriesDTO.genres())
                .seasonList(seriesDTO.seasonList())
                .poster(seriesDTO.poster())
                .backdrop(seriesDTO.backdrop())
                .reviewIds(series.getReviewIds()).build();
    }

    public static SeriesResponseDTO seriesToResponseDTO(Series series) {
        return new SeriesResponseDTO(series.getImdbId(), series.getTitle(), series.getNumberOfSeasons(), series.getReleaseDate(), series.getPoster());
    }

    // Review DTOs Converters
    public static Review dtoToReview(ReviewSaveDTO reviewSaveDTO) {
        return Review.builder()
                .title(reviewSaveDTO.title())
                .rating(reviewSaveDTO.rating())
                .body(reviewSaveDTO.body())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Review dtoToReviewUpdate(Review review, ReviewDTO reviewDTO) {
        return Review.builder()
                .id(review.getId())
                .title(reviewDTO.title())
                .rating(reviewDTO.rating())
                .body(reviewDTO.body())
                .createdAt(review.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
