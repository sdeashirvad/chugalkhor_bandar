package com.chugalkhorbandar.application.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class InMemorySessionStoreTest {

    @Test
    void findReturnsEmptyForExpiredSession() {
        InMemorySessionStore store = new InMemorySessionStore(List.of());
        store.setInactivityTimeout(Duration.ofMinutes(30));
        CurrentCharacter character = new CurrentCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null, null);
        Instant stale = Instant.now().minus(Duration.ofMinutes(31));
        store.register(new ChatSession("s1", character, stale, stale, SessionStatus.ACTIVE));

        assertThat(store.find("s1")).isEmpty();
    }
}
