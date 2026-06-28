package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.conversation.ConversationNotFoundException;
import com.chugalkhorbandar.application.conversation.ConversationService;
import com.chugalkhorbandar.application.session.SessionConstants;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ConversationController.class)
@Import(ApiExceptionHandler.class)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationService conversationService;

    @Test
    void createConversationReturns201() throws Exception {
        when(conversationService.startConversation("session-1"))
                .thenReturn(new Conversation(
                        "conversation-1",
                        "session-1",
                        new ConversationCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null),
                        Instant.parse("2026-06-27T12:00:00Z"),
                        Instant.parse("2026-06-27T12:00:00Z"),
                        ConversationStatus.ACTIVE,
                        List.of()));

        mockMvc.perform(post("/api/conversations").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conversationId").value("conversation-1"))
                .andExpect(jsonPath("$.currentCharacter.displayName").value("Alpha"));
    }

    @Test
    void sendMessageEchoesBandarReply() throws Exception {
        when(conversationService.appendUserMessage("session-1", "Hello"))
                .thenReturn(List.of(
                        new ConversationMessage(
                                "m1",
                                "conversation-1",
                                Sender.USER,
                                Instant.parse("2026-06-27T12:00:00Z"),
                                "Hello",
                                Visibility.PUBLIC,
                                Map.of()),
                        new ConversationMessage(
                                "m2",
                                "conversation-1",
                                Sender.BANDAR,
                                Instant.parse("2026-06-27T12:00:01Z"),
                                "I heard you.",
                                Visibility.PUBLIC,
                                Map.of())));

        mockMvc.perform(post("/api/conversations/current/messages")
                        .header(SessionConstants.SESSION_HEADER, "session-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].sender").value("USER"))
                .andExpect(jsonPath("$.messages[1].content").value("I heard you."));
    }

    @Test
    void currentConversationReturns404WhenMissing() throws Exception {
        when(conversationService.currentConversation(anyString())).thenThrow(new ConversationNotFoundException());

        mockMvc.perform(get("/api/conversations/current").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listMessagesReturnsConversationHistory() throws Exception {
        when(conversationService.getMessages("session-1"))
                .thenReturn(List.of(new ConversationMessage(
                        "m1",
                        "conversation-1",
                        Sender.USER,
                        Instant.parse("2026-06-27T12:00:00Z"),
                        "Hello",
                        Visibility.PUBLIC,
                        Map.of())));

        mockMvc.perform(get("/api/conversations/current/messages")
                        .header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].content").value("Hello"));
    }
}
