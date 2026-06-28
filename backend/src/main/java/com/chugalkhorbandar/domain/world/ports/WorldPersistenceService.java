package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.WorldRuntime;

public interface WorldPersistenceService {

    WorldUnitOfWork beginUnitOfWork();

    void persist(WorldRuntime runtime, WorldUnitOfWork unitOfWork);
}
