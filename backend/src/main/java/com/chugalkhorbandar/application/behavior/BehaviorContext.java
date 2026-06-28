package com.chugalkhorbandar.application.behavior;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BehaviorContext {

    private final ThreadLocal<BehaviorProfile> current = new ThreadLocal<>();

    public void activate(BehaviorProfile profile) {
        current.set(profile);
    }

    public Optional<BehaviorProfile> current() {
        return Optional.ofNullable(current.get());
    }

    public void clear() {
        current.remove();
    }
}
