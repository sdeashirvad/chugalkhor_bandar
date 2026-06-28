package com.chugalkhorbandar.application.context;

import com.chugalkhorbandar.application.conversation.ConversationService;
import com.chugalkhorbandar.application.query.TextSectionSupport;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ContextRequestFactory {

    private final SessionService sessionService;
    private final ConversationService conversationService;
    private final WorldStatusQueryService worldStatusQueryService;
    private final WorldRepositoryProvider repositoryProvider;

    public ContextRequestFactory(
            SessionService sessionService,
            ConversationService conversationService,
            WorldStatusQueryService worldStatusQueryService,
            WorldRepositoryProvider repositoryProvider) {
        this.sessionService = sessionService;
        this.conversationService = conversationService;
        this.worldStatusQueryService = worldStatusQueryService;
        this.repositoryProvider = repositoryProvider;
    }

    public ContextPlannerRequest create(String sessionId, String latestMessage) {
        ChatSession session = sessionService.requireSession(sessionId);
        Conversation conversation = conversationService.findCurrentConversation(sessionId).orElse(null);
        var worldStatus = worldStatusQueryService.getStatus();
        RuntimeWorldContext runtimeWorld = new RuntimeWorldContext(
                worldStatus.status(),
                worldStatus.bootstrapVersion(),
                worldStatus.characters(),
                worldStatus.stories(),
                knownEntityLabels());
        return new ContextPlannerRequest(
                session.currentCharacter(), session, conversation, latestMessage, runtimeWorld);
    }

    private List<String> knownEntityLabels() {
        Set<String> labels = new LinkedHashSet<>();
        List<RuntimeCharacter> characters = repositoryProvider.characters().findAll(CharacterQuery.all());
        for (RuntimeCharacter character : characters) {
            labels.add(character.title());
            labels.addAll(TextSectionSupport.parseListItems(character.sections().get("titles")));
        }
        repositoryProvider.places().findAll().forEach(place -> labels.add(place.title()));
        repositoryProvider.organizations().findAll().forEach(organization -> labels.add(organization.title()));
        return List.copyOf(new ArrayList<>(labels));
    }
}
