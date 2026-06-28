package com.chugalkhorbandar.application.cognition;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CognitiveAnalysisJsonParserTest {

    private CognitiveAnalysisJsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new CognitiveAnalysisJsonParser(new ObjectMapper());
    }

    @Test
    void parsesObservationsAndRecommendations() {
        String json =
                """
                {
                  "observations": [
                    {
                      "type": "PREFERENCE",
                      "confidence": 0.94,
                      "summary": "Hippu King enjoys historical stories.",
                      "evidence": "tell me a story"
                    }
                  ],
                  "recommendations": [
                    {
                      "action": "PROMOTE_TO_MEMORY",
                      "confidence": 0.91,
                      "reason": "Repeated across multiple conversations.",
                      "target": "character_alpha"
                    }
                  ]
                }
                """;
        CognitiveAnalysisJsonParser.ParsedAnalysis parsed =
                parser.parse(json, 0.5, Instant.parse("2026-06-01T12:00:00Z"));

        assertThat(parsed.observations()).hasSize(1);
        assertThat(parsed.observations().getFirst().type()).isEqualTo(ObservationType.PREFERENCE);
        assertThat(parsed.recommendations()).hasSize(1);
        assertThat(parsed.recommendations().getFirst().action()).isEqualTo(RecommendationAction.PROMOTE_TO_MEMORY);
    }

    @Test
    void filtersBelowMinimumConfidence() {
        String json =
                """
                {
                  "observations": [
                    {"type": "INTEREST", "confidence": 0.2, "summary": "low", "evidence": "x"}
                  ],
                  "recommendations": []
                }
                """;
        CognitiveAnalysisJsonParser.ParsedAnalysis parsed =
                parser.parse(json, 0.5, Instant.parse("2026-06-01T12:00:00Z"));

        assertThat(parsed.observations()).isEmpty();
    }
}
