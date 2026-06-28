CREATE TABLE memory_inbox_items (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    source VARCHAR(64) NOT NULL,
    source_id VARCHAR(255) NOT NULL,
    owner_character_id VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    importance VARCHAR(32) NOT NULL,
    confidence DOUBLE PRECISION NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    metadata_json TEXT NOT NULL,
    trace_json TEXT NOT NULL,
    analysis_id VARCHAR(255) NOT NULL,
    artifact_ids_json TEXT NOT NULL
);

CREATE INDEX idx_memory_inbox_owner ON memory_inbox_items (owner_character_id);
CREATE INDEX idx_memory_inbox_source ON memory_inbox_items (source, source_id);
CREATE INDEX idx_memory_inbox_status ON memory_inbox_items (status);
CREATE INDEX idx_memory_inbox_created_at ON memory_inbox_items (created_at);
