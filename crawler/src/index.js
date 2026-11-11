import cron from 'node-cron';
import { pool } from './db.js';
import { crawlSource } from './crawl.js';

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
let running = false;

// C4: tick every minute; R4 interval_min, R5 enabled, C6/R6 no overlapping ticks
async function tick() {
  if (running) return console.warn('[skip] previous tick still running');
  running = true;
  try {
    const { rows } = await pool.query(
      `SELECT * FROM source
        WHERE enabled
          AND (last_crawled_at IS NULL
               OR last_crawled_at < now() - make_interval(mins => interval_min))
        ORDER BY id`,
    );
    for (const src of rows) {
      console.log(new Date().toISOString(), await crawlSource(src));
      await sleep(1000);
    }
  } catch (e) {
    console.error('[tick error]', e.message);
  } finally {
    running = false;
  }
}

const expr = process.env.CRON ?? '* * * * *';
cron.schedule(expr, tick, { noOverlap: true });
console.log(`[scheduler] started (cron: ${expr})`);
