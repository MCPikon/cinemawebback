package com.mcpikon.pelisWebBack.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Season {
    private String overview;
    private List<Episode> episodeList;
    private String poster;
}
