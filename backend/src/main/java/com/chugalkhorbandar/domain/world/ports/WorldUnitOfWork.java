package com.chugalkhorbandar.domain.world.ports;

public interface WorldUnitOfWork {

    void begin();

    void commit();

    void rollback();
}
