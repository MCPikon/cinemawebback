package com.mcpikon.pelisWebBack.models;

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

@Document(collection = "series")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Series {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String imdbId;
    private String title;
    private String overview;
    private int numberOfSeasons;
    private String creator;
    private String releaseDate;
    private String trailerLink;
    private List<String> genres;
    private List<Season> seasonList;
    private String poster;
    private String backdrop;
    @DocumentReference
    private List<Review> reviewIds;
}