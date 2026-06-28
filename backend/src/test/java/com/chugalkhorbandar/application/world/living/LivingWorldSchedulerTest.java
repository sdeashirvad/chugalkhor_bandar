package com.chugalkhorbandar.application.world.living;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LivingWorldSchedulerTest {

    @Mock
    private LivingWorldProperties properties;

    @Mock
    private LivingWorldService livingWorldService;

    @InjectMocks
    private LivingWorldScheduler scheduler;

    @Test
    void skipsWhenDisabled() {
        when(properties.isEnabled()).thenReturn(false);

        scheduler.runScheduledWorldTick();

        verify(livingWorldService, never()).runScheduledTicks(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void runsScheduledTicksWhenEnabled() {
        when(properties.isEnabled()).thenReturn(true);

        scheduler.runScheduledWorldTick();

        verify(livingWorldService).runScheduledTicks(org.mockito.ArgumentMatchers.any());
    }
}
