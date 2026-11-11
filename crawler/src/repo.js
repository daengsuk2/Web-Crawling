import { createHash } from 'node:crypto';
import { pool } from './db.js';

const hashOf = (it) =>
  createHash('sha256').update(`${it.title}|${it.summary ?? ''}`).digest('hex');

// C3: UPSERT on url. R1 insert, R2 content changed -> updated_at, R3 unchanged -> last_seen_at only.
// The source that saved a url first owns it, so two sources listing the same article never overwrite each other.
export async function upsertArticle(sourceId, it) {
  const { rows } = await pool.query(
    `INSERT INTO article (source_id, url, title, summary, published_at, content_hash)
     VALUES ($1, $2, $3, $4, $5, $6)
     ON CONFLICT (url) DO UPDATE SET
       title        = EXCLUDED.title,
       summary      = EXCLUDED.summary,
       content_hash = EXCLUDED.content_hash,
       last_seen_at = now(),
       updated_at   = CASE WHEN (article.title, article.summary, article.content_hash)
                                IS DISTINCT FROM (EXCLUDED.title, EXCLUDED.summary, EXCLUDED.content_hash)
                           THEN now() ELSE article.updated_at END
     WHERE article.source_id = EXCLUDED.source_id
     RETURNING (xmax = 0) AS inserted,
               (article.updated_at = now()) AS changed`,
    [sourceId, it.url.slice(0, 1000), it.title.slice(0, 500), it.summary, it.publishedAt, hashOf(it)],
  );
  // no row: the url already belongs to another source, which stays its owner
  return rows[0] ?? { inserted: false, changed: false };
}

// C5: crawl_run history
export async function startRun(sourceId) {
  const { rows } = await pool.query(
    `INSERT INTO crawl_run (source_id, status) VALUES ($1, 'RUNNING') RETURNING id`,
    [sourceId],
  );
  return rows[0];
}

export async function finishRun(id, status, fetched, inserted, updated, error = null) {
  await pool.query(
    `UPDATE crawl_run
        SET finished_at = now(), status = $2, fetched_count = $3,
            inserted_count = $4, updated_count = $5, error_message = $6
      WHERE id = $1`,
    [id, status, fetched, inserted, updated, error],
  );
}

export async function touchSource(id) {
  await pool.query(`UPDATE source SET last_crawled_at = now() WHERE id = $1`, [id]);
}
