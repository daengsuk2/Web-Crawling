import { pool } from './db.js';
import { crawlSource } from './crawl.js';

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

// C7: crawl every enabled source once, ignoring interval_min
try {
  const { rows } = await pool.query(`SELECT * FROM source WHERE enabled ORDER BY id`);
  for (const src of rows) {
    console.log(await crawlSource(src));
    await sleep(1000);
  }
} finally {
  await pool.end();
}
