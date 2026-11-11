import { fetchRss, fetchHtml } from './fetchers.js';
import { startRun, finishRun, touchSource, upsertArticle } from './repo.js';

// Crawl one source (C5, R7, R8): a failure is recorded and never thrown to the caller
export async function crawlSource(src) {
  const run = await startRun(src.id);
  try {
    const items =
      src.type === 'RSS'
        ? await fetchRss(src.url)
        : await fetchHtml(src.url, src.list_selector);

    let inserted = 0;
    let updated = 0;
    for (const it of items) {
      const r = await upsertArticle(src.id, it);
      if (r.inserted) inserted++;
      else if (r.changed) updated++;
    }
    await finishRun(run.id, 'SUCCESS', items.length, inserted, updated);
    return { name: src.name, status: 'SUCCESS', fetched: items.length, inserted, updated };
  } catch (e) {
    await finishRun(run.id, 'FAILED', 0, 0, 0, e.message);
    return { name: src.name, status: 'FAILED', error: e.message };
  } finally {
    await touchSource(src.id);
  }
}
