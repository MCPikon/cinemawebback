package com.mcpikon.pelisWebBack.servicesImpl;

import com.mcpikon.pelisWebBack.entities.Review;
import com.mcpikon.pelisWebBack.entities.Series;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.Errors;
import com.mcpikon.pelisWebBack.repositories.MovieRepository;
import com.mcpikon.pelisWebBack.repositories.ReviewRepository;
import com.mcpikon.pelisWebBack.repositories.SeriesRepository;
import com.mcpikon.pelisWebBack.services.SeriesService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class SeriesServiceImpl implements SeriesService {
    @Autowired
    private SeriesRepository seriesRepo;

    @Autowired
    private MovieRepository moviesRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Override
    public List<Series> findAll() throws ErrorException {
        log.info("GET series /findAll executed");
        List<Series> seriesList = seriesRepo.findAll();
        if (seriesList.isEmpty()) throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);
        return seriesList;
    }

    @Override
    public Optional<Series> findById(ObjectId id) throws ErrorException {
        log.info("GET series /findById executed");
        return Optional.ofNullable(seriesRepo.findById(id).orElseThrow(() -> new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND)));
    }

    @Override
    public Optional<Series> findByImdbId(String imdbId) throws ErrorException {
        log.info("GET series /findByImdbId executed");
        return Optional.ofNullable(seriesRepo.findByImdbId(imdbId).orElseThrow(() -> new ErrorException(Errors.NOT_EXISTS, HttpStatus.NOT_FOUND)));
    }

    @Override
    public Series save(Series series) throws ErrorException {
        log.info("POST series /save executed");
        if (seriesRepo.existsByImdbId(series.getImdbId()) || moviesRepo.existsByImdbId(series.getImdbId()))
            throw new ErrorException(Errors.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        series.setReviewIds(new ArrayList<>());
        return seriesRepo.insert(series);
    }

    @Override
    public Map<String, String> delete(ObjectId id) throws ErrorException {
        log.info("DELETE series /delete executed");
        Series seriesToDelete = seriesRepo.findById(id).orElseThrow(() -> new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST));
        for (Review review : seriesToDelete.getReviewIds()) {
            reviewRepo.delete(review);
        }
        seriesRepo.delete(seriesToDelete);
        return Map.of("message", String.format("Series with id: '%s' was successfully deleted", id));
    }

    @Override
    public Series update(Series series) throws ErrorException {
        log.info("PUT series /update executed");
        if (!seriesRepo.existsById(series.getId())) throw new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST);
        return seriesRepo.save(series);
    }

    @Override
    public Series patch(ObjectId id, Map<String, String> fields) throws ErrorException {
        log.info("PATCH series /patch executed");
        Series seriesToPatch = seriesRepo.findById(id).orElseThrow(() -> new ErrorException(Errors.NOT_EXISTS, HttpStatus.BAD_REQUEST));
        fields.forEach((key, value) -> {
            if (key.equalsIgnoreCase("id") || key.equalsIgnoreCase("imdbId"))
                throw new ErrorException(Errors.ID_CANNOT_CHANGE, HttpStatus.BAD_REQUEST);
            Field field = ReflectionUtils.findField(Series.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, seriesToPatch, value);
            }
        });
        return seriesRepo.save(seriesToPatch);
    }
}
