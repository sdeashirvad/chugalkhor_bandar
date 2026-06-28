CREATE TABLE chronicles (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    category VARCHAR(32) NOT NULL,
    visibility VARCHAR(32) NOT NULL,
    confidence VARCHAR(32) NOT NULL,
    owner_character_id VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    chronicle_date DATE NOT NULL,
    metadata_json TEXT NOT NULL,
    provenance_json TEXT NOT NULL,
    version INT NOT NULL
);

CREATE INDEX idx_chronicles_created ON chronicles (created_at);
CREATE INDEX idx_chronicles_category ON chronicles (category);
CREATE INDEX idx_chronicles_visibility ON chronicles (visibility);
CREATE INDEX idx_chronicles_owner ON chronicles (owner_character_id);
CREATE INDEX idx_chronicles_date ON chronicles (chronicle_date);
