package com.chugalkhorbandar.application.context.knowledge;

import com.chugalkhorbandar.application.context.ContextReference;

public record KnowledgeFragmentRequest(
        KnowledgeFragmentType fragmentType,
        String reason,
        int priority,
        ContextReference reference) {}
