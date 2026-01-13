CREATE TABLE IF NOT EXISTS event_publication (
  id TEXT PRIMARY KEY,
  listener_id TEXT NOT NULL,
  event_type TEXT NOT NULL,
  serialized_event TEXT NOT NULL,
  publication_date TEXT NOT NULL,
  completion_date TEXT NULL,
  attempts INTEGER NOT NULL DEFAULT 0,
  last_error TEXT NULL
);

CREATE INDEX IF NOT EXISTS idx_event_pub_pending
  ON event_publication (completion_date, publication_date);

CREATE INDEX IF NOT EXISTS idx_event_pub_listener_pending
  ON event_publication (listener_id, completion_date, publication_date);
