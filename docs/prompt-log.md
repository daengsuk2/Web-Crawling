# Prompt log

AI prompts used during the project (date / prompt / result / what I fixed).

| Date | Prompt | Result | Notes |
|---|---|---|---|
| 2026-11-04 | Read the project PDF and summarize requirements | C1~C7, D1~D7, R1~R8 summary | Korean text in the PDF was partly lost in extraction |
| 2026-11-04 | Pick Japanese crawl targets (Mercari?) | Mercari rejected: list is rendered by JS, cheerio gets no items. Chose Zenn RSS | Checked robots.txt and RSS of 12 sites myself |
| 2026-11-11 | Scaffold folders, schema.sql, seed.sql, context.md | 4 tables, Zenn source, 10 keywords | Applied to PostgreSQL 16 in Docker, `\dt` shows 4 tables |
| 2026-11-11 | Implement C1~C3, C5, C7 (fetchers, repo, crawl, once) | 1st run 20 inserted, 2nd run 0 | Fixed: `RETURNING` could not detect changes, used `updated_at = now()` instead |
| 2026-11-11 | Same, with a 404 source | FAILED recorded, but `once` never exited | Fixed: rss-parser kept a connection open; switched to `fetch` + `parseString` |
| 2026-11-11 | Implement scheduler (C4, C6, R4, R5) | `index.js` with node-cron | Verified interval, enabled flag and overlap with a slow local feed |
