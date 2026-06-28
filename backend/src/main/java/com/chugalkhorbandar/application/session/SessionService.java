package com.chugalkhorbandar.application.session;

import com.chugalkhorbandar.application.query.EntityReferenceResolver;
import com.chugalkhorbandar.application.query.TextSectionSupport;
import com.chugalkhorbandar.application.notification.NotificationService;
import com.chugalkhorbandar.config.ChugalkhorProperties;
import com.chugalkhorbandar.domain.identity.ports.CharacterCredentialRepository;
import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.PlaceRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final WorldRepositoryProvider repositories;
    private final CharacterCredentialRepository credentials;
    private final InMemorySessionStore sessionStore;
    private final EntityReferenceResolver referenceResolver;
    private final ChugalkhorProperties properties;
    private final NotificationService notificationService;
    private final CharacterPresenceStore characterPresenceStore;

    public SessionService(
            WorldRepositoryProvider repositories,
            CharacterCredentialRepository credentials,
            EntityReferenceResolver referenceResolver,
            InMemorySessionStore sessionStore,
            ChugalkhorProperties properties,
            @Lazy NotificationService notificationService,
            CharacterPresenceStore characterPresenceStore) {
        this.repositories = repositories;
        this.credentials = credentials;
        this.referenceResolver = referenceResolver;
        this.sessionStore = sessionStore;
        this.properties = properties;
        this.notificationService = notificationService;
        this.characterPresenceStore = characterPresenceStore;
    }

    public ChatSession login(String animalName, String passkey) {
        RuntimeCharacter character = findCharacterByName(animalName)
                .orElseThrow(InvalidLoginException::new);
        String storedPasskey = credentials.findPasskeyByCharacterId(character.id())
                .orElseThrow(InvalidLoginException::new);
        if (!storedPasskey.equals(passkey)) {
            throw new InvalidLoginException();
        }
        ChatSession session = sessionStore.create(toCurrentCharacter(character));
        characterPresenceStore.recordSeen(character.id(), Instant.now());
        notificationService.generateOnLogin(session);
        return session;
    }

    public ChatSession requireSession(String sessionId) {
        return currentSession(sessionId).orElseThrow(UnauthorizedSessionException::new);
    }

    public Optional<ChatSession> currentSession(String sessionId) {
        return sessionStore.touch(sessionId);
    }

    public void logout(String sessionId) {
        sessionStore.remove(sessionId);
    }

    private CharacterRepository characters() {
        return repositories.characters();
    }

    private PlaceRepository places() {
        return repositories.places();
    }

    private Optional<RuntimeCharacter> findCharacterByName(String animalName) {
        return characters().findAll(CharacterQuery.all()).stream()
                .filter(character -> character.title().equalsIgnoreCase(animalName.trim()))
                .findFirst();
    }

    private CurrentCharacter toCurrentCharacter(RuntimeCharacter character) {
        List<String> titles = TextSectionSupport.parseListItems(character.sections().get("titles"));
        if (titles.isEmpty()) {
            titles = List.of(character.title());
        }
        String homeTerritory = resolveHomeTerritory(character);
        String currentLocation = character.currentPlaceId() == null
                ? null
                : places().findById(character.currentPlaceId()).map(place -> place.title()).orElse(null);
        return new CurrentCharacter(
                character.id(),
                character.title(),
                titles,
                TextSectionSupport.extractSpecies(character.sections()),
                homeTerritory,
                currentLocation);
    }

    private String resolveHomeTerritory(RuntimeCharacter character) {
        String homeTerritoryId = character.preferences().get("homeTerritoryId");
        if (homeTerritoryId != null && !homeTerritoryId.isBlank()) {
            return referenceResolver
                    .resolveTerritory(homeTerritoryId)
                    .map(EntityReferenceResolver.ResolvedReference::name)
                    .orElse(null);
        }
        if (character.currentPlaceId() == null) {
            return null;
        }
        return places().findById(character.currentPlaceId())
                .flatMap(referenceResolver::resolveTerritoryForPlace)
                .map(EntityReferenceResolver.ResolvedReference::name)
                .orElse(null);
    }
}
