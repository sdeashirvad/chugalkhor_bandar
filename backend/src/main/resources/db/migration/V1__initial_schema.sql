CREATE TABLE characters (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL,
    current_place_id VARCHAR(255),
    preferences TEXT NOT NULL
);

CREATE TABLE territories (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL,
    current_ruler_id VARCHAR(255)
);

CREATE TABLE places (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL
);

CREATE TABLE stories (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL,
    linked_stories TEXT NOT NULL
);

CREATE TABLE relationships (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL
);

CREATE TABLE organizations (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL,
    roles TEXT NOT NULL
);

CREATE TABLE resources (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL,
    available_quantity INTEGER NOT NULL
);

CREATE TABLE objects (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL,
    owner_id VARCHAR(255)
);

CREATE TABLE timeline_entries (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    chronology_id VARCHAR(255) NOT NULL,
    title VARCHAR(500) NOT NULL,
    timeline_entries TEXT NOT NULL,
    sections TEXT NOT NULL,
    recorded_at TIMESTAMP
);

CREATE TABLE prompt_profiles (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL
);

CREATE TABLE canon_entries (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL
);

CREATE TABLE world_rules (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL
);

CREATE TABLE laws (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL
);

CREATE TABLE customs (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL
);

CREATE TABLE glossary_entries (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    sections TEXT NOT NULL
);
