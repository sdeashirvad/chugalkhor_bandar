package com.chugalkhorbandar.adapters.api;

import com.chugalkhorbandar.adapters.api.dto.ApiErrorDto;
import com.chugalkhorbandar.application.notification.NotificationNotFoundException;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactNotFoundException;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisNotFoundException;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxNotFoundException;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationNotFoundException;
import com.chugalkhorbandar.application.chronicle.ChronicleNotFoundException;
import com.chugalkhorbandar.application.world.living.WorldEventNotFoundException;
import com.chugalkhorbandar.application.reporting.ReportingNotFoundException;
import com.chugalkhorbandar.application.behavior.BehaviorProfileNotFoundException;
import com.chugalkhorbandar.application.conversation.ConversationNotFoundException;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanNotFoundException;
import com.chugalkhorbandar.application.llm.ProviderException;
import com.chugalkhorbandar.application.query.ResourceNotFoundException;
import com.chugalkhorbandar.application.session.InvalidLoginException;
import com.chugalkhorbandar.application.session.UnauthorizedSessionException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", exception.getMessage(), request);
    }

    @ExceptionHandler(ConversationPlanNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleConversationPlanNotFound(
            ConversationPlanNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "No conversation plan found for session", request);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotificationNotFound(
            NotificationNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "Notification not found", request);
    }

    @ExceptionHandler(ConversationArtifactNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleConversationArtifactNotFound(
            ConversationArtifactNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "Conversation artifact not found", request);
    }

    @ExceptionHandler(CognitiveAnalysisNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleCognitiveAnalysisNotFound(
            CognitiveAnalysisNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "Cognitive analysis not found", request);
    }

    @ExceptionHandler(MemoryInboxNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleMemoryInboxNotFound(
            MemoryInboxNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "Memory inbox item not found", request);
    }

    @ExceptionHandler(MemoryConsolidationNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleMemoryConsolidationNotFound(
            MemoryConsolidationNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "Memory consolidation report not found", request);
    }

    @ExceptionHandler(ReportingNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleReportingNotFound(
            ReportingNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "Reporting resource not found", request);
    }

    @ExceptionHandler(ChronicleNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleChronicleNotFound(
            ChronicleNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "Chronicle not found", request);
    }

    @ExceptionHandler(WorldEventNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleWorldEventNotFound(
            WorldEventNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "World event or tick not found", request);
    }

    @ExceptionHandler(BehaviorProfileNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleBehaviorProfileNotFound(
            BehaviorProfileNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", "No behavior profile found for session", request);
    }

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleConversationNotFound(
            ConversationNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ApiErrorDto> handleInvalidLogin(InvalidLoginException exception, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Unauthorized", exception.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedSessionException.class)
    public ResponseEntity<ApiErrorDto> handleUnauthorizedSession(
            UnauthorizedSessionException exception, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Unauthorized", exception.getMessage(), request);
    }

    @ExceptionHandler(ProviderException.class)
    public ResponseEntity<ApiErrorDto> handleProviderFailure(ProviderException exception, HttpServletRequest request) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", exception.getMessage(), request);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorDto> handleUnsupportedOperation(
            UnsupportedOperationException exception, HttpServletRequest request) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", exception.getMessage(), request);
    }

    private static ResponseEntity<ApiErrorDto> buildError(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        ApiErrorDto body = new ApiErrorDto(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
