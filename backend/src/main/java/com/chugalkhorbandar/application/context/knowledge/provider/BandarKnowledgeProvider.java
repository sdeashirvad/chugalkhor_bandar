package com.chugalkhorbandar.application.context.knowledge.provider;



import com.chugalkhorbandar.application.context.ContextPlannerRequest;

import com.chugalkhorbandar.application.context.ContextReference;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentMappings;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPriorities;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentRequest;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;

import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;

import java.util.ArrayList;

import java.util.List;

import java.util.Locale;

import java.util.Map;

import java.util.Optional;

import java.util.Set;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;



@Component

public class BandarKnowledgeProvider implements KnowledgeProvider {



    private static final String BANDAR_PERSONALITY_ID = "prompt_bandar_personality";

    private static final Pattern CHARACTER_SECTION = Pattern.compile("(?m)^##\\s+(.+?)\\s*$");

    private static final Set<KnowledgeFragmentType> SUPPORTED = Set.of(

            KnowledgeFragmentType.IDENTITY,

            KnowledgeFragmentType.PERSONALITY,

            KnowledgeFragmentType.SPEAKING_STYLE,

            KnowledgeFragmentType.STORYTELLING,

            KnowledgeFragmentType.HUMOR,

            KnowledgeFragmentType.SECRET_POLICY,

            KnowledgeFragmentType.CHARACTER_OPINION,

            KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR);



    private final WorldRepositoryProvider repositories;



    public BandarKnowledgeProvider(WorldRepositoryProvider repositories) {

        this.repositories = repositories;

    }



    @Override

    public String providerName() {

        return "bandarKnowledge";

    }



    @Override

    public Set<KnowledgeFragmentType> supportedFragmentTypes() {

        return SUPPORTED;

    }



    @Override

    public List<KnowledgeFragmentRequest> plan(ContextPlannerRequest request, Set<KnowledgeFragmentType> selectedTypes) {

        List<KnowledgeFragmentRequest> requests = new ArrayList<>();

        for (KnowledgeFragmentType type : selectedTypes) {

            if (!SUPPORTED.contains(type)) {

                continue;

            }

            requests.add(new KnowledgeFragmentRequest(

                    type,

                    "Bandar personality fragment",

                    KnowledgeFragmentPriorities.priority(type),

                    new ContextReference(

                            providerName(),

                            "promptProfile",

                            BANDAR_PERSONALITY_ID,

                            type.name().toLowerCase(),

                            KnowledgeFragmentPriorities.priority(type))));

        }

        return requests;

    }



    @Override

    public Optional<KnowledgeFragment> resolve(KnowledgeFragmentRequest request, ContextPlannerRequest context) {

        if (request.fragmentType() == KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR) {

            return resolveRelationshipToBandar(context);

        }

        return repositories.promptProfiles().findById(BANDAR_PERSONALITY_ID).flatMap(profile -> profile.sections().entrySet().stream()

                .map(entry -> Map.entry(

                        KnowledgeFragmentMappings.personalitySectionType(entry.getKey()), entry))

                .filter(entry -> entry.getKey() == request.fragmentType())

                .findFirst()

                .map(entry -> KnowledgeFragment.of(

                        request.fragmentType(),

                        humanTitle(request.fragmentType()),

                        entry.getValue().getValue(),

                        BANDAR_PERSONALITY_ID,

                        entry.getValue().getKey(),

                        KnowledgeFragmentMappings.tagsFor(request.fragmentType(), BANDAR_PERSONALITY_ID),

                        1.0)));

    }



    private Optional<KnowledgeFragment> resolveRelationshipToBandar(ContextPlannerRequest context) {

        String characterName = context.currentCharacter().displayName();

        return repositories.promptProfiles().findById(BANDAR_PERSONALITY_ID).flatMap(profile -> {

            Optional<String> attitudeSection = profile.sections().entrySet().stream()

                    .filter(entry -> entry.getKey().toLowerCase(Locale.ROOT).contains("attitude"))

                    .map(Map.Entry::getValue)

                    .findFirst();

            if (attitudeSection.isEmpty()) {

                return Optional.empty();

            }

            String relationshipContent = extractCharacterAttitude(attitudeSection.get(), characterName)

                    .orElse("You know " + characterName + " and speak with them as a familiar friend of the Jungle.");

            return Optional.of(KnowledgeFragment.of(

                    KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR,

                    "Relationship to Bandar",

                    relationshipContent.trim(),

                    BANDAR_PERSONALITY_ID,

                    "relationshipToBandar",

                    KnowledgeFragmentMappings.tagsFor(KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR, context.currentCharacter().id()),

                    1.0));

        });

    }



    private static Optional<String> extractCharacterAttitude(String sectionContent, String characterName) {

        if (sectionContent == null || sectionContent.isBlank()) {

            return Optional.empty();

        }

        Matcher matcher = CHARACTER_SECTION.matcher(sectionContent);

        String currentHeading = null;

        StringBuilder currentBody = new StringBuilder();

        while (matcher.find()) {

            if (currentHeading != null && namesMatch(currentHeading, characterName)) {

                return Optional.of(currentBody.toString().trim());

            }

            if (currentHeading != null) {

                currentBody.setLength(0);

            }

            currentHeading = matcher.group(1).trim();

            int bodyStart = matcher.end();

            int nextHeading = sectionContent.indexOf("\n## ", bodyStart);

            String body = nextHeading < 0

                    ? sectionContent.substring(bodyStart)

                    : sectionContent.substring(bodyStart, nextHeading);

            currentBody.append(body.trim());

        }

        if (currentHeading != null && namesMatch(currentHeading, characterName)) {

            return Optional.of(currentBody.toString().trim());

        }

        return Optional.empty();

    }



    private static boolean namesMatch(String heading, String characterName) {

        return heading.equalsIgnoreCase(characterName.trim());

    }



    private static String humanTitle(KnowledgeFragmentType type) {

        return switch (type) {

            case IDENTITY -> "Bandar Identity";

            case SPEAKING_STYLE -> "Bandar Speaking Style";

            case STORYTELLING -> "Bandar Storytelling Style";

            case HUMOR -> "Bandar Sense of Humor";

            case SECRET_POLICY -> "Bandar Secret Policy";

            case CHARACTER_OPINION -> "Bandar Character Opinions";

            case PERSONALITY -> "Bandar Core Personality";

            case RELATIONSHIP_TO_BANDAR -> "Relationship to Bandar";

            default -> type.name();

        };

    }

}


