package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextResolveResult;
import com.chugalkhorbandar.application.context.ContextResolveService;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlan;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlanningTrace;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import com.chugalkhorbandar.application.session.SessionConstants;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ContextPlanController.class)
@Import(ApiExceptionHandler.class)
class ContextResolveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContextResolveService contextResolveService;

    @MockitoBean
    private com.chugalkhorbandar.application.context.ContextPlanService contextPlanService;

    @Test
    void resolveReturnsFragments() throws Exception {
        KnowledgeFragment fragment = KnowledgeFragment.of(
                KnowledgeFragmentType.IDENTITY,
                "Bandar Identity",
                "I am Bandar",
                "prompt_bandar_personality",
                "identity",
                Set.of(),
                1.0);
        when(contextResolveService.resolve(eq("session-1"), eq("Hello")))
                .thenReturn(new ContextResolveResult(
                        new ContextPlan(
                                List.of(),
                                new KnowledgeFragmentPlan(List.of(), 0, new KnowledgeFragmentPlanningTrace(List.of())),
                                0,
                                new com.chugalkhorbandar.application.context.ContextPlanningTrace(List.of())),
                        new ResolvedContext(
                                List.of(ResolvedContextSection.from(
                                        new com.chugalkhorbandar.application.context.ContextSection(
                                                ContextSectionType.PERSONALITY,
                                                10,
                                                "promptProfiles",
                                                new ContextReference(
                                                        "promptProfiles",
                                                        "promptProfile",
                                                        "prompt_bandar_personality",
                                                        "sections",
                                                        10),
                                                5),
                                        "I am Bandar")),
                                List.of(fragment),
                                10)));

        mockMvc.perform(post("/api/context/resolve")
                        .header(SessionConstants.SESSION_HEADER, "session-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latestMessage\":\"Hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fragments[0].fragmentType").value("IDENTITY"))
                .andExpect(jsonPath("$.fragments[0].content").value("I am Bandar"));
    }
}
