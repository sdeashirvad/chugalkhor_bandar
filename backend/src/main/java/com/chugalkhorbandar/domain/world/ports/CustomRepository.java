package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import java.util.List;
import java.util.Optional;

public interface CustomRepository {

    void create(RuntimeCustom custom);

    boolean exists(String customId);

    Optional<RuntimeCustom> findById(String customId);

    List<RuntimeCustom> findAll();
}
