package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.util.List;
import java.util.Optional;

public interface CharacterRepository {

    void create(RuntimeCharacter character);

    void update(RuntimeCharacter character);

    void delete(String characterId);

    boolean exists(String characterId);

    Optional<RuntimeCharacter> findById(String characterId);

    List<RuntimeCharacter> findAll(CharacterQuery query);

    void moveCharacter(String characterId, String fromPlaceId, String toPlaceId);

    void changePreference(String characterId, String preferenceKey, String preferenceValue);

    void assignTitle(String characterId, String title);

    void transferOwnership(String characterId, String newOwnerId);
}
