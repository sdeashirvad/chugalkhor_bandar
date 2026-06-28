package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeLaw;
import java.util.List;
import java.util.Optional;

public interface LawRepository {

    void create(RuntimeLaw law);

    boolean exists(String lawId);

    Optional<RuntimeLaw> findById(String lawId);

    List<RuntimeLaw> findAll();
}
