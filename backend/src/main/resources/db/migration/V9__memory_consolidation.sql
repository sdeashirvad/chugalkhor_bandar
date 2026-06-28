CREATE TABLE memory_consolidation_reports (
    run_id VARCHAR(255) NOT NULL PRIMARY KEY,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NOT NULL,
    duration_ms BIGINT NOT NULL,
    processed INT NOT NULL,
    promoted INT NOT NULL,
    discarded INT NOT NULL,
    expired INT NOT NULL,
    archived INT NOT NULL,
    pending INT NOT NULL,
    candidate_count INT NOT NULL,
    summary TEXT NOT NULL,
    txt_report TEXT NOT NULL,
    json_report TEXT NOT NULL,
    reflection TEXT NOT NULL,
    email_status VARCHAR(32) NOT NULL,
    email_error TEXT NOT NULL
);

CREATE TABLE long_term_memory_candidates (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    run_id VARCHAR(255) NOT NULL,
    owner_character_id VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    importance VARCHAR(32) NOT NULL,
    reason TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    source_inbox_item_ids_json TEXT NOT NULL,
    metadata_json TEXT NOT NULL
);

CREATE INDEX idx_consolidation_reports_started ON memory_consolidation_reports (started_at);
CREATE INDEX idx_ltm_candidates_run ON long_term_memory_candidates (run_id);
CREATE INDEX idx_ltm_candidates_owner ON long_term_memory_candidates (owner_character_id);
CREATE INDEX idx_ltm_candidates_created ON long_term_memory_candidates (created_at);
