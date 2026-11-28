ALTER TABLE projects
    ADD COLUMN next_issue_number INTEGER NOT NULL DEFAULT 1;

UPDATE pbis
SET type = UPPER(type)
WHERE type IN ('feature', 'bug', 'technical');

ALTER TABLE pbis
    DROP CONSTRAINT chk_pbis_type,
    ADD CONSTRAINT chk_pbis_type CHECK (type IN ('FEATURE', 'BUG', 'TECHNICAL')),
    DROP CONSTRAINT chk_pbis_priority,
    ADD CONSTRAINT chk_pbis_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'HIGHEST'));
