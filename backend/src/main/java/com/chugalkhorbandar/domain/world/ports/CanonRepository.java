package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeCanon;
import java.util.List;
import java.util.Optional;

public interface CanonRepository {

    void create(RuntimeCanon canon);

    boolean exists(String canonId);

    Optional<RuntimeCanon> findById(String canonId);

    List<RuntimeCanon> findAll();
}
