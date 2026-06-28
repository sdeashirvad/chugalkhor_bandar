package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeGlossaryEntry;
import java.util.List;
import java.util.Optional;

public interface GlossaryRepository {

    void create(RuntimeGlossaryEntry entry);

    boolean exists(String glossaryId);

    Optional<RuntimeGlossaryEntry> findById(String glossaryId);

    List<RuntimeGlossaryEntry> findAll();
}
