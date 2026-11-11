-- Tech Radar schema (owned by crawler; Spring uses ddl-auto=validate)

CREATE TABLE IF NOT EXISTS source (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    type             VARCHAR(10)  NOT NULL CHECK (type IN ('RSS', 'HTML')),
    url              VARCHAR(500) NOT NULL UNIQUE,
    list_selector    VARCHAR(200),
    enabled          BOOLEAN      NOT NULL DEFAULT TRUE,
    interval_min     INT          NOT NULL DEFAULT 10 CHECK (interval_min >= 1),
    last_crawled_at  TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS article (
    id             BIGSERIAL PRIMARY KEY,
    source_id      BIGINT       NOT NULL REFERENCES source(id),
    url            VARCHAR(1000) NOT NULL UNIQUE,
    title          VARCHAR(500) NOT NULL,
    summary        TEXT,
    published_at   TIMESTAMPTZ,
    content_hash   VARCHAR(64),
    first_seen_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_seen_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_article_first_seen ON article(first_seen_at);
CREATE INDEX IF NOT EXISTS idx_article_source ON article(source_id);

CREATE TABLE IF NOT EXISTS crawl_run (
    id              BIGSERIAL PRIMARY KEY,
    source_id       BIGINT      NOT NULL REFERENCES source(id),
    started_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at     TIMESTAMPTZ,
    status          VARCHAR(10) NOT NULL CHECK (status IN ('RUNNING', 'SUCCESS', 'FAILED')),
    fetched_count   INT NOT NULL DEFAULT 0,
    inserted_count  INT NOT NULL DEFAULT 0,
    updated_count   INT NOT NULL DEFAULT 0,
    error_message   TEXT
);
CREATE INDEX IF NOT EXISTS idx_crawl_run_started ON crawl_run(started_at DESC);

CREATE TABLE IF NOT EXISTS tracked_keyword (
    id          BIGSERIAL PRIMARY KEY,
    word        VARCHAR(50) NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
