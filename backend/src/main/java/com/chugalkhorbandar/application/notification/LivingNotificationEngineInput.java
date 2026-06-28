package com.chugalkhorbandar.application.notification;

import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import java.time.Instant;
import java.util.List;

public record LivingNotificationEngineInput(
        CurrentCharacter currentUser,
        RuntimeWorldContext runtimeWorld,
        WorkingMemory workingMemory,
        ChatSession session,
        Instant currentTime,
        List<Notification> existingNotifications,
        Instant lastNotificationAt) {}
