package com.mcpikon.cinemawebback.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Review(String title, String body, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
