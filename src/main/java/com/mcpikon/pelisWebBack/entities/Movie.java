package com.mcpikon.pelisWebBack.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String imdbId;
    private String title;
    private String overview;
    private String duration;
    private String director;
    private String releaseDate;
    private String trailerLink;
    private List<String> genres;
    private String poster;
    private String backdrop;
    @DocumentReference
    private List<Review> reviewIds;

    public Movie(String imdbId, String title, String overview, String duration, String director,
                 String releaseDate, String trailerLink, List<String> genres, String poster, String backdrop) {
        this.imdbId = imdbId;
        this.title = title;
        this.overview = overview;
        this.duration = duration;
        this.director = director;
        this.releaseDate = releaseDate;
        this.trailerLink = trailerLink;
        this.genres = genres;
        this.poster = poster;
        this.backdrop = backdrop;
    }
}
