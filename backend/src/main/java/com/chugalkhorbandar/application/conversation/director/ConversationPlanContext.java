package com.chugalkhorbandar.application.conversation.director;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ConversationPlanContext {

    private final ThreadLocal<ConversationPlanExecution> current = new ThreadLocal<>();

    public record ConversationPlanExecution(ConversationPlan plan, int replyIndex, int totalReplies) {}

    public void activate(ConversationPlan plan, int replyIndex) {
        current.set(new ConversationPlanExecution(plan, replyIndex, plan.expectedMessageCount()));
    }

    public Optional<ConversationPlanExecution> current() {
        return Optional.ofNullable(current.get());
    }

    public void clear() {
        current.remove();
    }
}
