package com.mcpikon.pelisWebBack.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Episode {
    private String title;
    private String releaseDate;
    private String duration;
    private String description;
}
