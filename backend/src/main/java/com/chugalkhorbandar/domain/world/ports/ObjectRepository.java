package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeObject;
import java.util.List;
import java.util.Optional;

public interface ObjectRepository {

    void create(RuntimeObject object);

    boolean exists(String objectId);

    Optional<RuntimeObject> findById(String objectId);

    List<RuntimeObject> findByOwner(String ownerId);

    void transferOwnership(String objectId, String fromOwnerId, String toOwnerId);
}
