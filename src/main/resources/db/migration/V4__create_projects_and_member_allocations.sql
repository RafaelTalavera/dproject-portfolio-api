CREATE TABLE portfolio.projects (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    expected_end_date DATE NOT NULL,
    actual_end_date DATE,
    total_budget NUMERIC(19, 2) NOT NULL,
    description TEXT,
    manager_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_projects_manager
        FOREIGN KEY (manager_id) REFERENCES portfolio.members (id),
    CONSTRAINT ck_projects_total_budget_positive
        CHECK (total_budget > 0)
);

CREATE TABLE portfolio.project_members (
    project_id UUID NOT NULL,
    member_id UUID NOT NULL,
    CONSTRAINT pk_project_members PRIMARY KEY (project_id, member_id),
    CONSTRAINT fk_project_members_project
        FOREIGN KEY (project_id) REFERENCES portfolio.projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_project_members_member
        FOREIGN KEY (member_id) REFERENCES portfolio.members (id)
);

CREATE INDEX idx_projects_status ON portfolio.projects (status);
CREATE INDEX idx_projects_manager_id ON portfolio.projects (manager_id);
CREATE INDEX idx_projects_start_date ON portfolio.projects (start_date);
CREATE INDEX idx_project_members_member_project ON portfolio.project_members (member_id, project_id);
