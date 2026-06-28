package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import java.util.List;
import java.util.Optional;

public interface PlaceRepository {

    void create(RuntimePlace place);

    boolean exists(String placeId);

    Optional<RuntimePlace> findById(String placeId);

    List<RuntimePlace> findAll();
}
