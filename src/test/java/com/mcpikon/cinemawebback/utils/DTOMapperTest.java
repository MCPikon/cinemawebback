package com.mcpikon.cinemawebback.utils;

import com.mcpikon.cinemawebback.dtos.*;
import com.mcpikon.cinemawebback.models.Movie;
import com.mcpikon.cinemawebback.models.Review;
import com.mcpikon.cinemawebback.models.Series;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DTOMapperTest {
    private Movie movieMock;
    private MovieDTO movieDTOMock;
    private Series seriesMock;
    private SeriesDTO seriesDTOMock;
    private Review reviewMock;
    private ReviewDTO reviewDTOMock;
    private ReviewSaveDTO reviewSaveDTOMock;

    @BeforeEach
    void init() {
        movieMock = Movie.builder().id(new ObjectId()).imdbId("tt12345").title("movie test").build();
        movieDTOMock = MovieDTO.builder().imdbId("tt12345").title("movie test").build();
        seriesMock = Series.builder().id(new ObjectId()).imdbId("tt54321").title("series test").build();
        seriesDTOMock = SeriesDTO.builder().imdbId("tt54321").title("series test").build();
        reviewMock = Review.builder().id(new ObjectId()).title("review test").body("review to test").build();
        reviewDTOMock = ReviewDTO.builder().title("review test").body("review to test").build();
        reviewSaveDTOMock = ReviewSaveDTO.builder().imdbId("tt24681").title("review test").body("review to test").build();
    }

    @Test
    @DisplayName("DTO to Movie")
    void dtoToMovie_thenReturnsMovie() {
        Movie movie = DTOMapper.dtoToMovie(movieDTOMock);
        assertNotNull(movie);
        assertEquals(movieDTOMock.imdbId(), movie.getImdbId());
    }

    @Test
    @DisplayName("DTO to Movie Update")
    void dtoToMovieUpdate_thenReturnsMovie() {
        Movie movie = DTOMapper.dtoToMovieUpdate(movieMock, movieDTOMock);
        assertNotNull(movie);
        assertEquals(movieMock.getId(), movie.getId());
        assertEquals(movieDTOMock.title(), movie.getTitle());
    }

    @Test
    @DisplayName("Movie To ResponseDTO")
    void movieToResponseDTO_thenReturnsMovie() {
        MovieResponseDTO movieResponseDTO = DTOMapper.movieToResponseDTO(movieMock);
        assertNotNull(movieResponseDTO);
        assertEquals(movieMock.getImdbId(), movieResponseDTO.imdbId());
    }

    @Test
    @DisplayName("DTO to Series")
    void dtoToSeries_thenReturnsSeries() {
        Series series = DTOMapper.dtoToSeries(seriesDTOMock);
        assertNotNull(series);
        assertEquals(seriesDTOMock.imdbId(), series.getImdbId());
    }

    @Test
    @DisplayName("DTO to Series Update")
    void dtoToSeriesUpdate_thenReturnsSeries() {
        Series series = DTOMapper.dtoToSeriesUpdate(seriesMock, seriesDTOMock);
        assertNotNull(series);
        assertEquals(seriesMock.getId(), series.getId());
        assertEquals(seriesDTOMock.title(), series.getTitle());
    }

    @Test
    @DisplayName("Series to ResponseDTO")
    void seriesToResponseDTO_thenReturnsSeries() {
        SeriesResponseDTO seriesResponseDTO = DTOMapper.seriesToResponseDTO(seriesMock);
        assertNotNull(seriesResponseDTO);
        assertEquals(seriesMock.getImdbId(), seriesResponseDTO.imdbId());
    }

    @Test
    @DisplayName("DTO to Review")
    void dtoToReview_thenReturnsReview() {
        Review review = DTOMapper.dtoToReview(reviewSaveDTOMock);
        assertNotNull(review);
        assertEquals(reviewSaveDTOMock.title(), review.getTitle());
    }

    @Test
    @DisplayName("DTO to Review Update")
    void dtoToReviewUpdate_thenReturnsReview() {
        Review review = DTOMapper.dtoToReviewUpdate(reviewMock, reviewDTOMock);
        assertNotNull(review);
        assertEquals(reviewMock.getId(), review.getId());
        assertEquals(reviewDTOMock.title(), review.getTitle());
    }
}