CREATE TABLE working_memory (
    session_id VARCHAR(255) NOT NULL PRIMARY KEY,
    active_topic VARCHAR(255) NOT NULL,
    conversation_mood VARCHAR(64) NOT NULL,
    current_story VARCHAR(512),
    active_entities TEXT NOT NULL,
    unanswered_questions TEXT NOT NULL,
    recent_promises TEXT NOT NULL,
    important_facts TEXT NOT NULL,
    field_traces TEXT NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    version BIGINT NOT NULL
);

CREATE INDEX idx_working_memory_last_updated ON working_memory (last_updated);
