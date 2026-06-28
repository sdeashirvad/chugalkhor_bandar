package com.chugalkhorbandar.application.world.living;

import java.util.Optional;

public class LivingWorldGenerationStore {

    private volatile LivingWorldTickResult latestTick;

    public void save(LivingWorldTickResult result) {
        this.latestTick = result;
    }

    public Optional<LivingWorldTickResult> getLatest() {
        return Optional.ofNullable(latestTick);
    }
}
