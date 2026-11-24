UPDATE users SET updated_at = created_at WHERE updated_at IS NULL;

ALTER TABLE users
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at TYPE TIMESTAMPTZ USING updated_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at SET DEFAULT NOW(),
    ALTER COLUMN updated_at SET NOT NULL,
    ALTER COLUMN last_login_at TYPE TIMESTAMPTZ USING last_login_at AT TIME ZONE 'UTC';

CREATE TABLE projects (
    id UUID NOT NULL,
    created_by UUID NOT NULL,
    name VARCHAR(128) NOT NULL,
    code_prefix VARCHAR(16) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_projects PRIMARY KEY (id),
    CONSTRAINT fk_projects_created_by_users FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT uq_projects_code_prefix UNIQUE (code_prefix)
);

CREATE TABLE members (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(32) NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_members PRIMARY KEY (id),
    CONSTRAINT fk_members_project_id_projects FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_members_user_id_users FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_members_project_id_user_id UNIQUE (project_id, user_id)
);
CREATE INDEX idx_members_project_id ON members(project_id);
CREATE INDEX idx_members_user_id ON members(user_id);

CREATE TABLE backlogs (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_backlogs PRIMARY KEY (id),
    CONSTRAINT fk_backlogs_project_id_projects FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT uq_backlogs_project_id UNIQUE (project_id)
);

CREATE TABLE sprints (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(128) NOT NULL,
    goal TEXT,
    status VARCHAR(32) NOT NULL,
    start_date DATE,
    end_date DATE,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_sprints PRIMARY KEY (id),
    CONSTRAINT fk_sprints_project_id_projects FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT chk_sprints_status CHECK (status IN ('planned', 'active', 'completed'))
);
CREATE INDEX idx_sprints_project_id ON sprints(project_id);

CREATE TABLE epics (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ,
    CONSTRAINT pk_epics PRIMARY KEY (id),
    CONSTRAINT fk_epics_project_id_projects FOREIGN KEY (project_id) REFERENCES projects(id)
);
CREATE INDEX idx_epics_project_id_active ON epics(project_id) WHERE archived_at IS NULL;

CREATE TABLE pbis (
    id UUID NOT NULL,
    backlog_id UUID NOT NULL,
    epic_id UUID,
    code VARCHAR(32) NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    priority VARCHAR(16) NOT NULL,
    move_to_top_at TIMESTAMPTZ,
    story_points INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ,
    CONSTRAINT pk_pbis PRIMARY KEY (id),
    CONSTRAINT fk_pbis_backlog_id_backlogs FOREIGN KEY (backlog_id) REFERENCES backlogs(id),
    CONSTRAINT fk_pbis_epic_id_epics FOREIGN KEY (epic_id) REFERENCES epics(id),
    CONSTRAINT uq_pbis_backlog_code UNIQUE (backlog_id, code),
    CONSTRAINT chk_pbis_type CHECK (type IN ('feature', 'bug', 'technical')),
    CONSTRAINT chk_pbis_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))
);
CREATE INDEX idx_pbis_backlog_id_active ON pbis(backlog_id) WHERE archived_at IS NULL;
CREATE INDEX idx_pbis_epic_id ON pbis(epic_id);

CREATE TABLE sprint_pbis (
    id UUID NOT NULL,
    sprint_id UUID NOT NULL,
    pbi_id UUID NOT NULL,
    added_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_sprint_pbis PRIMARY KEY (id),
    CONSTRAINT fk_sprint_pbis_sprint_id_sprints FOREIGN KEY (sprint_id) REFERENCES sprints(id),
    CONSTRAINT fk_sprint_pbis_pbi_id_pbis FOREIGN KEY (pbi_id) REFERENCES pbis(id),
    CONSTRAINT uq_sprint_pbis_sprint_id_pbi_id UNIQUE (sprint_id, pbi_id)
);
CREATE INDEX idx_sprint_pbis_pbi_id ON sprint_pbis(pbi_id);

CREATE TABLE tasks (
    id UUID NOT NULL,
    pbi_id UUID NOT NULL,
    assignee_id UUID,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL,
    is_debt BOOLEAN NOT NULL DEFAULT FALSE,
    start_date DATE,
    due_date DATE,
    priority INTEGER NOT NULL,
    position INTEGER NOT NULL DEFAULT 0,
    estimated_time_hour NUMERIC(10,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_pbi_id_pbis FOREIGN KEY (pbi_id) REFERENCES pbis(id),
    CONSTRAINT fk_tasks_assignee_id_users FOREIGN KEY (assignee_id) REFERENCES users(id),
    CONSTRAINT chk_tasks_estimated_time_hour CHECK (estimated_time_hour IS NULL OR estimated_time_hour >= 0)
);
CREATE INDEX idx_tasks_pbi_id_active ON tasks(pbi_id) WHERE archived_at IS NULL;
CREATE INDEX idx_tasks_assignee_id ON tasks(assignee_id);

CREATE TABLE checklists (
    id UUID NOT NULL,
    task_id UUID NOT NULL,
    name VARCHAR(256) NOT NULL,
    is_done BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_checklists PRIMARY KEY (id),
    CONSTRAINT fk_checklists_task_id_tasks FOREIGN KEY (task_id) REFERENCES tasks(id)
);
CREATE INDEX idx_checklists_task_id ON checklists(task_id);

CREATE TABLE worklogs (
    id UUID NOT NULL,
    task_id UUID NOT NULL,
    assignee_id UUID NOT NULL,
    time_hours NUMERIC(10,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_worklogs PRIMARY KEY (id),
    CONSTRAINT fk_worklogs_task_id_tasks FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_worklogs_assignee_id_users FOREIGN KEY (assignee_id) REFERENCES users(id),
    CONSTRAINT chk_worklogs_time_hours CHECK (time_hours > 0)
);
CREATE INDEX idx_worklogs_task_id ON worklogs(task_id);

CREATE TABLE comments (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    pbi_id UUID,
    task_id UUID,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_user_id_users FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comments_pbi_id_pbis FOREIGN KEY (pbi_id) REFERENCES pbis(id),
    CONSTRAINT fk_comments_task_id_tasks FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT chk_comments_exactly_one_target CHECK ((pbi_id IS NOT NULL) <> (task_id IS NOT NULL))
);
CREATE INDEX idx_comments_pbi_id ON comments(pbi_id);
CREATE INDEX idx_comments_task_id ON comments(task_id);
