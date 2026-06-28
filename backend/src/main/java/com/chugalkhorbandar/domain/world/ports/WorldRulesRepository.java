package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeWorldRules;
import java.util.List;
import java.util.Optional;

public interface WorldRulesRepository {

    void create(RuntimeWorldRules worldRules);

    void update(RuntimeWorldRules worldRules);

    boolean exists(String worldRulesId);

    Optional<RuntimeWorldRules> findById(String worldRulesId);

    List<RuntimeWorldRules> findAll();
}
