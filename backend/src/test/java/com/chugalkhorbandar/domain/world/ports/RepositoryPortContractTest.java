package com.chugalkhorbandar.domain.world.ports;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.ports.query.RelationshipQuery;
import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import com.chugalkhorbandar.domain.world.ports.query.TimelineQuery;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class RepositoryPortContractTest {

    private static final Set<String> EXPECTED_REPOSITORIES = Set.of(
            "CharacterRepository",
            "TerritoryRepository",
            "PlaceRepository",
            "StoryRepository",
            "RelationshipRepository",
            "OrganizationRepository",
            "ResourceRepository",
            "ObjectRepository",
            "TimelineRepository",
            "PromptProfileRepository",
            "CanonRepository",
            "WorldRulesRepository",
            "LawRepository",
            "CustomRepository",
            "GlossaryRepository");

    private static final List<Class<?>> REPOSITORY_PORTS = List.of(
            CharacterRepository.class,
            TerritoryRepository.class,
            PlaceRepository.class,
            StoryRepository.class,
            RelationshipRepository.class,
            OrganizationRepository.class,
            ResourceRepository.class,
            ObjectRepository.class,
            TimelineRepository.class,
            PromptProfileRepository.class,
            CanonRepository.class,
            WorldRulesRepository.class,
            LawRepository.class,
            CustomRepository.class,
            GlossaryRepository.class);

    @Test
    void definesRepositoryPortForEachAggregate() {
        Set<String> found = REPOSITORY_PORTS.stream().map(Class::getSimpleName).collect(Collectors.toSet());

        assertThat(found).containsExactlyInAnyOrderElementsOf(EXPECTED_REPOSITORIES);
    }

    @Test
    void doesNotDefineWorldRepository() {
        boolean worldRepositoryExists = REPOSITORY_PORTS.stream()
                .anyMatch(type -> type.getSimpleName().equals("WorldRepository"));

        assertThat(worldRepositoryExists).isFalse();
    }

    @Test
    void repositoryPortsAreInterfacesWithoutImplementations() {
        for (Class<?> type : REPOSITORY_PORTS) {
            assertThat(Modifier.isInterface(type.getModifiers())).isTrue();
            assertThat(type.getInterfaces()).isEmpty();
            assertThat(type.getPermittedSubclasses()).isNull();
        }
    }

    @Test
    void worldRepositoryProviderExposesAllAggregateRepositories() {
        Set<String> providerMethods = Arrays.stream(WorldRepositoryProvider.class.getMethods())
                .filter(method -> !method.getDeclaringClass().equals(Object.class))
                .map(Method::getName)
                .collect(Collectors.toSet());

        assertThat(providerMethods)
                .containsExactlyInAnyOrder(
                        "characters",
                        "territories",
                        "places",
                        "stories",
                        "relationships",
                        "organizations",
                        "resources",
                        "objects",
                        "timeline",
                        "promptProfiles",
                        "canon",
                        "worldRules",
                        "laws",
                        "customs",
                        "glossary");
    }

    @Test
    void worldUnitOfWorkDefinesTransactionBoundary() {
        List<String> methods = Arrays.stream(WorldUnitOfWork.class.getMethods())
                .filter(method -> !method.getDeclaringClass().equals(Object.class))
                .map(Method::getName)
                .toList();

        assertThat(methods).containsExactlyInAnyOrder("begin", "commit", "rollback");
    }

    @Test
    void worldPersistenceServiceDefinesPersistenceContract() {
        assertThat(WorldPersistenceService.class.isInterface()).isTrue();
        assertThat(WorldPersistenceService.class.getMethods())
                .extracting(Method::getName)
                .contains("beginUnitOfWork", "persist");
    }

    @Test
    void querySpecificationsAreImmutableRecords() {
        assertThat(CharacterQuery.class.isRecord()).isTrue();
        assertThat(StoryQuery.class.isRecord()).isTrue();
        assertThat(TimelineQuery.class.isRecord()).isTrue();
        assertThat(RelationshipQuery.class.isRecord()).isTrue();
    }

    @Test
    void characterRepositoryExposesDomainOperationsNotGenericCrud() {
        List<String> methods = Arrays.stream(CharacterRepository.class.getMethods()).map(Method::getName).toList();

        assertThat(methods)
                .contains(
                        "create",
                        "update",
                        "delete",
                        "exists",
                        "findById",
                        "findAll",
                        "moveCharacter",
                        "changePreference",
                        "assignTitle",
                        "transferOwnership");
        assertThat(methods).doesNotContain("save", "saveAll", "deleteAll", "findByIdAndDelete");
    }
}
