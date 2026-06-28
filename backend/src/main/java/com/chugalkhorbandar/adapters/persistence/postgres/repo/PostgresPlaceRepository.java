package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.PlaceJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.PlaceMapper;
import com.chugalkhorbandar.domain.world.ports.PlaceRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import java.util.List;
import java.util.Optional;

public final class PostgresPlaceRepository implements PlaceRepository {

    private final PlaceJpaRepository jpa;

    public PostgresPlaceRepository(PlaceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimePlace place) {
        ensureAbsent(jpa, place.id(), "Places");
        jpa.save(PlaceMapper.toEntity(place));
    }

    @Override
    public boolean exists(String placeId) {
        return jpa.existsById(placeId);
    }

    @Override
    public Optional<RuntimePlace> findById(String placeId) {
        return jpa.findById(placeId).map(PlaceMapper::toRuntime);
    }

    @Override
    public List<RuntimePlace> findAll() {
        return jpa.findAll().stream().map(PlaceMapper::toRuntime).toList();
    }
}
