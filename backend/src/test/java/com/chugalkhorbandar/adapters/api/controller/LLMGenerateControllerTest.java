package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.llm.LLMGenerateResult;
import com.chugalkhorbandar.application.llm.LLMProviderInfo;
import com.chugalkhorbandar.application.llm.LLMProviderType;
import com.chugalkhorbandar.application.llm.LLMService;
import com.chugalkhorbandar.application.llm.MockLLMProvider;
import com.chugalkhorbandar.config.LlmProperties;
import com.chugalkhorbandar.application.llm.ProviderMessage;
import com.chugalkhorbandar.application.llm.ProviderMessageRole;
import com.chugalkhorbandar.application.llm.ProviderRequest;
import com.chugalkhorbandar.application.llm.ProviderResponse;
import com.chugalkhorbandar.application.llm.ProviderTokenUsage;
import com.chugalkhorbandar.application.session.SessionConstants;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = LLMGenerateController.class)
@Import(ApiExceptionHandler.class)
class LLMGenerateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LLMService llmService;

    @Test
    void generateReturnsProviderRequestAndResponse() throws Exception {
        MockLLMProvider mockProvider = new MockLLMProvider(new LlmProperties());
        ProviderRequest request = new ProviderRequest(
                List.of(ProviderMessage.of(ProviderMessageRole.USER, "Where am I?", "USER_MESSAGE")),
                Map.of("sectionCount", "1"),
                0.7,
                1024,
                "mock-bandar");
        ProviderResponse response = mockProvider.generateReply(request);

        when(llmService.generate(eq("session-1"), eq("Where am I?")))
                .thenReturn(new LLMGenerateResult(
                        new LLMProviderInfo(
                                LLMProviderType.MOCK,
                                "Mock Bandar",
                                "Developer inspection provider",
                                true,
                                "mock-bandar"),
                        request,
                        response));

        mockMvc.perform(post("/api/llm/generate")
                        .header(SessionConstants.SESSION_HEADER, "session-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latestMessage\":\"Where am I?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider.type").value("MOCK"))
                .andExpect(jsonPath("$.request.messages[0].role").value("USER"))
                .andExpect(jsonPath("$.request.messages[0].content").value("Where am I?"))
                .andExpect(jsonPath("$.response.reply").value(org.hamcrest.Matchers.containsString("[Mock Bandar]")))
                .andExpect(jsonPath("$.response.finishReason").value("stop"));
    }
}
