ALTER TABLE tasks ADD COLUMN agent_id INTEGER REFERENCES agents(id);

CREATE INDEX IF NOT EXISTS idx_tasks_agent_id ON tasks(agent_id);
