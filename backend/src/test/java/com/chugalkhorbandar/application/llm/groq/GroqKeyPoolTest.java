package com.chugalkhorbandar.application.llm.groq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class GroqKeyPoolTest {

    @Test
    void loadsConfiguredKeysAndIgnoresBlankEntries() {
        GroqKeyPool pool = new GroqKeyPool(List.of(" key-one ", "", "  ", "key-two"));

        assertThat(pool.keyCount()).isEqualTo(2);
    }

    @Test
    void rotatesKeysInRoundRobinOrder() {
        GroqKeyPool pool = new GroqKeyPool(List.of("key-one", "key-two"));

        assertThat(pool.acquireKey().displayIndex()).isEqualTo(1);
        assertThat(pool.acquireKey().displayIndex()).isEqualTo(2);
        assertThat(pool.acquireKey().displayIndex()).isEqualTo(1);
    }

    @Test
    void providesAlternateKeyForRetry() {
        GroqKeyPool pool = new GroqKeyPool(List.of("key-one", "key-two"));

        assertThat(pool.alternateKey(1)).contains(new GroqKeyPool.GroqKeySelection(2, "key-two"));
        assertThat(pool.alternateKey(2)).contains(new GroqKeyPool.GroqKeySelection(1, "key-one"));
    }

    @Test
    void alternateKeyUnavailableWithSingleKey() {
        GroqKeyPool pool = new GroqKeyPool(List.of("only-key"));

        assertThat(pool.alternateKey(1)).isEmpty();
    }

    @Test
    void rejectsAcquireWhenNoKeysConfigured() {
        GroqKeyPool pool = new GroqKeyPool(List.of("", "  "));

        assertThat(pool.keyCount()).isZero();
        assertThatThrownBy(pool::acquireKey).isInstanceOf(IllegalStateException.class);
    }
}
