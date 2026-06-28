package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.llm.ProviderCapabilities;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptComposer;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.application.prompt.budget.BudgetAllocator;
import com.chugalkhorbandar.application.prompt.budget.BudgetedPrompt;
import com.chugalkhorbandar.application.prompt.budget.PromptBudgetResult;
import com.chugalkhorbandar.application.prompt.budget.PromptBudgetService;
import com.chugalkhorbandar.application.prompt.profile.PromptProfileService;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileCatalog;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileType;
import com.chugalkhorbandar.application.prompt.profile.ProfileSelection;
import com.chugalkhorbandar.application.session.SessionConstants;
import com.chugalkhorbandar.config.PromptProfileProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PromptProfileController.class)
@Import(ApiExceptionHandler.class)
class PromptBudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromptProfileService promptProfileService;

    @MockitoBean
    private PromptBudgetService promptBudgetService;

    @Test
    void budgetReturnsAllocationDetails() throws Exception {
        PromptProfileProperties properties = new PromptProfileProperties();
        var profile = new ContextProfileCatalog(properties).profile(ContextProfileType.LOCATION_QUERY);
        ProviderCapabilities capabilities = new ProviderCapabilities(8192, 1024, true, true);
        ComposedPrompt composed = new ComposedPrompt(List.of(
                PromptSection.of(PromptSectionType.CURRENT_LOCATION, "Current Location", false, "Hippu Palace"),
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?"),
                PromptSection.of(
                        PromptSectionType.INSTRUCTIONS,
                        "Instructions",
                        true,
                        PromptComposer.DEFAULT_INSTRUCTION)));
        BudgetedPrompt budgeted = new BudgetAllocator(properties)
                .allocate(composed, profile, capabilities);

        when(promptBudgetService.allocate(eq("session-1"), eq("Where am I?")))
                .thenReturn(new PromptBudgetResult(
                        new ProfileSelection(profile, "User message contains \"where\""), budgeted, capabilities));

        mockMvc.perform(post("/api/prompt/budget")
                        .header(SessionConstants.SESSION_HEADER, "session-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latestMessage\":\"Where am I?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.type").value("LOCATION_QUERY"))
                .andExpect(jsonPath("$.sections").isArray())
                .andExpect(jsonPath("$.budget.totalAvailableTokens").value(7168))
                .andExpect(jsonPath("$.providerCapabilities.maxContextTokens").value(8192));
    }
}
