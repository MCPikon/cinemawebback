package com.mcpikon.pelisWebBack.repositories;

import com.mcpikon.pelisWebBack.entities.Movie;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DataMongoTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepo;

    @DisplayName("TEST - Save Movie")
    @Test
    void testSave() {
        Movie movieToSave = new Movie(new ObjectId(), "tt213123", "Prueba", "Descripción", "1h",
                "Director", "2024-1-1", "https://google.es",
                new ArrayList<>(), "poster", "backdrop", new ArrayList<>());
        Movie movie = movieRepo.save(movieToSave);
        assertNotNull(movie);
        assertEquals(movieToSave, movie);
    }

    // TODO: continuar haciendo el testing de los demás métodos
}