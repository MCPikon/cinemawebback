package com.mcpikon.pelisWebBack.servicesImpl;

import com.mcpikon.pelisWebBack.entities.Series;
import com.mcpikon.pelisWebBack.models.ErrorException;
import com.mcpikon.pelisWebBack.models.Errors;
import com.mcpikon.pelisWebBack.repositories.SeriesRepository;
import com.mcpikon.pelisWebBack.services.SeriesService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class SeriesServiceImpl implements SeriesService {
    @Autowired
    private SeriesRepository seriesRepo;

    @Override
    public List<Series> findAll() throws ErrorException {
        log.info("GET series /findAll executed");
        List<Series> seriesList = seriesRepo.findAll();
        if (seriesList.isEmpty()) throw new ErrorException(Errors.EMPTY, HttpStatus.NO_CONTENT);
        return seriesList;
    }

    // TODO: Implementar métodos para los endpoints GET (id & imdbId), POST, PUT, PATCH y DELETE

    @Override
    public Optional<Series> findById(ObjectId id) throws ErrorException {
        log.info("GET series /findById executed");
        return Optional.empty();
    }

    @Override
    public Optional<Series> findByImdbId(String imdbId) throws ErrorException {
        log.info("GET series /findByImdbId executed");
        return Optional.empty();
    }

    @Override
    public Series save(Series series) throws ErrorException {
        log.info("POST series /save executed");
        return null;
    }

    @Override
    public Map<String, String> delete(ObjectId id) throws ErrorException {
        log.info("DELETE series /delete executed");
        return null;
    }

    @Override
    public Series update(Series series) throws ErrorException {
        log.info("PUT series /update executed");
        return null;
    }

    @Override
    public Series patch(ObjectId id, Map<String, String> fields) throws ErrorException {
        log.info("PATCH series /patch executed");
        return null;
    }
}
