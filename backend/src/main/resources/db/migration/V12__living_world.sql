CREATE TABLE world_events (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    title TEXT NOT NULL,
    summary TEXT NOT NULL,
    participants_json TEXT NOT NULL,
    visibility VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    effective_date DATE NOT NULL,
    metadata_json TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    origin VARCHAR(64) NOT NULL
);

CREATE TABLE world_tick_history (
    run_id VARCHAR(255) NOT NULL PRIMARY KEY,
    mode VARCHAR(32) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NOT NULL,
    duration_ms BIGINT NOT NULL,
    world_date DATE NOT NULL,
    events_generated INT NOT NULL,
    artifacts_generated INT NOT NULL,
    notifications_generated INT NOT NULL,
    generator_names_json TEXT NOT NULL,
    event_ids_json TEXT NOT NULL,
    artifact_ids_json TEXT NOT NULL,
    notification_ids_json TEXT NOT NULL
);

CREATE INDEX idx_world_events_created ON world_events (created_at);
CREATE INDEX idx_world_events_type ON world_events (type);
CREATE INDEX idx_world_tick_history_started ON world_tick_history (started_at);
CREATE INDEX idx_world_tick_history_mode ON world_tick_history (mode);
