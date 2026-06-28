package com.chugalkhorbandar.application.conversation.director;

public final class ConversationPlanInstructionBuilder {

    private ConversationPlanInstructionBuilder() {}

    public static String build(ConversationPlan plan, int replyIndex, int totalReplies) {
        if (plan == null) {
            return "";
        }
        StringBuilder content = new StringBuilder();
        content.append("Conversation Goal\n\n");
        content.append(goalInstruction(plan));
        appendArcGuidance(content, plan, replyIndex, totalReplies);
        if (totalReplies > 1) {
            content.append("\n\nThis is reply ")
                    .append(replyIndex + 1)
                    .append(" of ")
                    .append(totalReplies)
                    .append(".");
            if (replyIndex == totalReplies - 1) {
                content.append("\nBring this sequence to a natural close.");
            } else {
                content.append("\nContinue naturally from your previous reply.");
            }
        }
        if (plan.tellStory()) {
            content.append("\nPrefer storytelling over exposition.");
        }
        if (plan.tellJoke()) {
            content.append("\nA light joke or playful remark is welcome.");
        }
        if (plan.tellMemory()) {
            content.append("\nRefer to a relevant memory from this conversation if helpful.");
        }
        if (plan.askFollowUpQuestion()) {
            content.append("\nYou may end with a reflective remark or gentle tease — not a counselling question.");
        } else if (replyIndex == totalReplies - 1 && !plan.continueConversation()) {
            content.append("\nDo not extend the conversation.");
        }
        if (plan.endConversation()) {
            content.append("\nAnswer briefly and let the conversation end gracefully.");
        }
        if (plan.suggestedTone() != null && !plan.suggestedTone().isBlank()) {
            content.append("\nSuggested tone: ").append(plan.suggestedTone()).append(".");
        }
        return content.toString().trim();
    }

    private static void appendArcGuidance(
            StringBuilder content, ConversationPlan plan, int replyIndex, int totalReplies) {
        if (totalReplies <= 1) {
            return;
        }
        if (plan.conversationArc() == ConversationArc.QUESTION_STORY
                || plan.conversationArc() == ConversationArc.STORY_CONTINUATION) {
            if (totalReplies >= 3) {
                if (replyIndex == 0) {
                    content.append("\nBegin with a brief introduction — set the scene lightly.");
                } else if (replyIndex == 1) {
                    content.append("\nDeliver the main story body.");
                } else if (replyIndex == totalReplies - 1) {
                    content.append("\nClose with reflection or a gentle follow-up.");
                }
            } else if (replyIndex == 0) {
                content.append("\nOpen with a small introduction.");
            } else if (replyIndex == totalReplies - 1) {
                content.append("\nContinue with the main story or a reflective close.");
            }
        }
    }

    private static String goalInstruction(ConversationPlan plan) {
        return switch (plan.goal()) {
            case GREETING -> "Welcome the speaker warmly.\nEstablish rapport.\nInvite them to speak.";
            case ANSWER -> "Answer the speaker directly.\nStay concise and clear.";
            case LOCATION_HELP -> "Help the speaker understand where they are.\nUse known location context.";
            case IDENTITY -> "Answer using the Current Speaker information.\nDo not ask them to identify themselves.";
            case STORY -> "Tell a story from memory.\nBegin with a scene.\nEnd with varied curiosity — not always a question.";
            case CHEER_UP -> "React as Bandar first — a smile, gentle tease, or playful memory.\nPrefer storytelling over counselling.";
            case REMEMBER -> "Acknowledge what the speaker wants remembered.\nConfirm briefly without claiming permanent memory.";
            case GOODBYE -> "Answer briefly.\nDo not extend the conversation.";
            case SMALL_TALK -> "Reply conversationally as a storyteller.\nReact warmly before probing.\nKeep the exchange light and natural.";
            case QUESTION -> "Answer the question thoughtfully.\nAdmit uncertainty if needed.";
            case CONTINUE_STORY -> "Continue the story already in progress.\nMaintain narrative momentum.";
            case REMINDER -> "Acknowledge the reminder request.\nRespond helpfully within this conversation.";
            case UNKNOWN -> "Respond helpfully based on available context.";
        };
    }
}
