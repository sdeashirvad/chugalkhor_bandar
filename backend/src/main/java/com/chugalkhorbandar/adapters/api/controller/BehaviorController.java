package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.BehaviorProfileResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.BehaviorDtoMapper;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.behavior.BehaviorProfileNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/behavior")
@Tag(name = "Behavior Engine", description = "Deterministic conversational style selection (developer)")
public class BehaviorController {

    private final BehaviorEngineService behaviorEngineService;

    public BehaviorController(BehaviorEngineService behaviorEngineService) {
        this.behaviorEngineService = behaviorEngineService;
    }

    @GetMapping("/current")
    @Operation(summary = "Return the current BehaviorProfile for the active session")
    public BehaviorProfileResponseDto currentProfile(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return BehaviorDtoMapper.toDto(behaviorEngineService
                .getCurrentProfile(sessionId)
                .orElseThrow(BehaviorProfileNotFoundException::new));
    }
}
