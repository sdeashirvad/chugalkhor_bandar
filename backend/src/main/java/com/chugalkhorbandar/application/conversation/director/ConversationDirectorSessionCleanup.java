package com.chugalkhorbandar.application.conversation.director;

import com.chugalkhorbandar.application.session.SessionExpiredListener;
import org.springframework.stereotype.Component;

@Component
public class ConversationDirectorSessionCleanup implements SessionExpiredListener {

    private final InMemoryConversationPlanStore store;

    public ConversationDirectorSessionCleanup(InMemoryConversationPlanStore store) {
        this.store = store;
    }

    @Override
    public void onSessionExpired(String sessionId) {
        store.deleteBySessionId(sessionId);
    }
}
