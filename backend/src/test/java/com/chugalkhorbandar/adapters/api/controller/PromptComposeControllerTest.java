package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.llm.PromptToProviderAdapter;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptComposeService;
import com.chugalkhorbandar.application.prompt.PromptComposer;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.application.session.SessionConstants;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PromptComposeController.class)
@Import({ApiExceptionHandler.class, PromptToProviderAdapter.class, LlmProperties.class})
class PromptComposeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromptComposeService promptComposeService;

    @Test
    void composeReturnsStructuredPromptSections() throws Exception {
        when(promptComposeService.compose(eq("session-1"), eq("Where am I?")))
                .thenReturn(new ComposedPrompt(List.of(
                        PromptSection.of(PromptSectionType.CURRENT_USER, "Current User", true, "Hippu King"),
                        PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?"),
                        PromptSection.of(
                                PromptSectionType.INSTRUCTIONS,
                                "Instructions",
                                true,
                                PromptComposer.DEFAULT_INSTRUCTION))));

        mockMvc.perform(post("/api/prompt/compose")
                        .header(SessionConstants.SESSION_HEADER, "session-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latestMessage\":\"Where am I?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections[0].sectionType").value("CURRENT_USER"))
                .andExpect(jsonPath("$.sections[1].sectionType").value("USER_MESSAGE"))
                .andExpect(jsonPath("$.sections[2].sectionType").value("INSTRUCTIONS"))
                .andExpect(jsonPath("$.totalEstimatedTokens").isNumber())
                .andExpect(jsonPath("$.inspection.requiredSectionCount").value(3))
                .andExpect(jsonPath("$.llmMessages[0].content").value(org.hamcrest.Matchers.containsString("The Current Speaker")));
    }
}
