package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.prompt.profile.ContextProfile;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileCatalog;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileType;
import com.chugalkhorbandar.application.prompt.profile.ProfileSelection;
import com.chugalkhorbandar.application.prompt.profile.PromptProfileResult;
import com.chugalkhorbandar.application.prompt.profile.PromptProfileService;
import com.chugalkhorbandar.application.session.SessionConstants;
import com.chugalkhorbandar.config.PromptProfileProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PromptProfileController.class)
@Import(ApiExceptionHandler.class)
class PromptProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromptProfileService promptProfileService;

    @MockitoBean
    private com.chugalkhorbandar.application.prompt.budget.PromptBudgetService promptBudgetService;

    @Test
    void profileReturnsSelectedProfile() throws Exception {
        ContextProfile profile = new ContextProfileCatalog(new PromptProfileProperties())
                .profile(ContextProfileType.LOCATION_QUERY);
        when(promptProfileService.selectProfile(eq("session-1"), eq("Where am I?")))
                .thenReturn(new PromptProfileResult(
                        new ProfileSelection(profile, "User message contains \"where\""), null));

        mockMvc.perform(post("/api/prompt/profile")
                        .header(SessionConstants.SESSION_HEADER, "session-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latestMessage\":\"Where am I?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.type").value("LOCATION_QUERY"))
                .andExpect(jsonPath("$.selectionReason").value(org.hamcrest.Matchers.containsString("where")));
    }
}
