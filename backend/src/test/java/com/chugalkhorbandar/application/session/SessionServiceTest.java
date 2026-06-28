package com.chugalkhorbandar.application.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryCharacterCredentialRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.notification.NotificationService;
import com.chugalkhorbandar.application.query.EntityReferenceResolver;
import com.chugalkhorbandar.config.ChugalkhorProperties;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    private final InMemoryWorldStore store = new InMemoryWorldStore();
    private final InMemoryCharacterCredentialRepository credentials = new InMemoryCharacterCredentialRepository();
    private final InMemorySessionStore sessionStore = new InMemorySessionStore(List.of());
    private final ChugalkhorProperties properties = new ChugalkhorProperties();

    @Mock
    private NotificationService notificationService;

    private SessionService sessionService;

    @BeforeEach
    void seedCharacter() {
        sessionService = new SessionService(
                new InMemoryWorldRepositoryProvider(store),
                credentials,
                new EntityReferenceResolver(new InMemoryWorldRepositoryProvider(store)),
                sessionStore,
                properties,
                notificationService,
                new CharacterPresenceStore());
        store.characters()
                .put(
                        "character_alpha",
                        new RuntimeCharacter(
                                "character_alpha",
                                "Alpha",
                                Map.of("titles", "- Alpha\n", "basicInformation", "| Species | Rabbitu |"),
                                null,
                                Map.of()));
        credentials.save("character_alpha", "secret");
        sessionStore.setInactivityTimeout(Duration.ofMinutes(30));
    }

    @Test
    void loginSucceedsWithValidCredentials() {
        ChatSession session = sessionService.login("Alpha", "secret");

        assertThat(session.currentCharacter().displayName()).isEqualTo("Alpha");
        assertThat(session.status()).isEqualTo(SessionStatus.ACTIVE);
        verify(notificationService).generateOnLogin(session);
    }

    @Test
    void loginFailsWithInvalidPasskey() {
        assertThatThrownBy(() -> sessionService.login("Alpha", "wrong"))
                .isInstanceOf(InvalidLoginException.class);
    }

    @Test
    void loginFailsWithUnknownCharacter() {
        assertThatThrownBy(() -> sessionService.login("Missing", "secret"))
                .isInstanceOf(InvalidLoginException.class);
    }

    @Test
    void logoutRemovesSession() {
        ChatSession session = sessionService.login("Alpha", "secret");
        sessionService.logout(session.sessionId());
        assertThat(sessionService.currentSession(session.sessionId())).isEmpty();
    }

    @Test
    void resolvesHomeTerritoryFromCharacterPreferences() {
        store.territories()
                .put(
                        "territory_alpha",
                        new com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory(
                                "territory_alpha", "Alpha Kingdom", Map.of(), null));
        store.characters()
                .put(
                        "character_alpha",
                        new RuntimeCharacter(
                                "character_alpha",
                                "Alpha",
                                Map.of("titles", "- Alpha\n", "basicInformation", "| Species | Rabbitu |"),
                                "place_alpha_home",
                                Map.of("homeTerritoryId", "territory_alpha")));
        store.places()
                .put(
                        "place_alpha_home",
                        new com.chugalkhorbandar.domain.world.runtime.RuntimePlace(
                                "place_alpha_home", "Alpha Home", Map.of("locatedIn", "territory_alpha")));

        ChatSession session = sessionService.login("Alpha", "secret");

        assertThat(session.currentCharacter().homeTerritory()).isEqualTo("Alpha Kingdom");
    }

    @Test
    void expiredSessionIsRejected() {
        CurrentCharacter character = new CurrentCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null);
        Instant stale = Instant.now().minus(Duration.ofMinutes(31));
        sessionStore.register(new ChatSession("expired-session", character, stale, stale, SessionStatus.ACTIVE));

        assertThat(sessionService.currentSession("expired-session")).isEmpty();
        assertThatThrownBy(() -> sessionService.requireSession("expired-session"))
                .isInstanceOf(UnauthorizedSessionException.class);
    }
}
