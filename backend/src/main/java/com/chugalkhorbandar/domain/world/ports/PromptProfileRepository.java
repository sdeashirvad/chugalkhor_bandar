package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import java.util.List;
import java.util.Optional;

public interface PromptProfileRepository {

    void create(RuntimePromptProfile profile);

    void update(RuntimePromptProfile profile);

    boolean exists(String profileId);

    Optional<RuntimePromptProfile> findById(String profileId);

    List<RuntimePromptProfile> findAll();
}
