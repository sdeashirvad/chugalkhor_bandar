package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleCategory;
import com.chugalkhorbandar.application.chronicle.ChronicleConfidence;
import com.chugalkhorbandar.application.chronicle.ChronicleProvenance;
import com.chugalkhorbandar.application.chronicle.ChronicleVisibility;
import com.chugalkhorbandar.application.chronicle.ChronicleWriteResult;
import com.chugalkhorbandar.application.chronicle.ChronicleWriterProperties;
import com.chugalkhorbandar.application.chronicle.ChronicleWriterService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ChronicleController.class)
@Import(ApiExceptionHandler.class)
class ChronicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChronicleWriterService chronicleWriterService;

    @MockitoBean
    private ChronicleWriterProperties properties;

    @Test
    void listReturnsChronicles() throws Exception {
        when(chronicleWriterService.listChronicles()).thenReturn(List.of(sampleChronicle()));

        mockMvc.perform(get("/api/chronicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("chron-candidate-1-v1"))
                .andExpect(jsonPath("$[0].category").value("PROMISE"));
    }

    @Test
    void writeEndpointTriggersWriter() throws Exception {
        when(properties.isDeveloperWriteEnabled()).thenReturn(true);
        when(chronicleWriterService.writeChronicles()).thenReturn(sampleWriteResult());

        mockMvc.perform(post("/api/chronicles/dev/write"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.chroniclesWritten").value(1));
    }

    private static Chronicle sampleChronicle() {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        return new Chronicle(
                "chron-candidate-1-v1",
                "Promise: Lost Crown",
                ChronicleCategory.PROMISE,
                ChronicleVisibility.PRIVATE,
                ChronicleConfidence.OFFICIAL,
                "character_alpha",
                "Lost Crown story",
                "Bandar promised Hippu King that he would tell the Lost Crown story.",
                now,
                LocalDate.parse("2026-06-01"),
                Map.of("type", "PROMISE"),
                new ChronicleProvenance(
                        "conv-1",
                        List.of("artifact-1"),
                        List.of(),
                        List.of("inbox-1"),
                        "run-1",
                        "candidate-1",
                        "chron-candidate-1-v1",
                        List.of(),
                        Map.of()),
                1);
    }

    private static ChronicleWriteResult sampleWriteResult() {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        return new ChronicleWriteResult("write-run-1", now, now, 10, 1, 1, 0, List.of(sampleChronicle()), List.of());
    }
}
