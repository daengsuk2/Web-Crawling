# Project context (YNC Tech Radar)

## Stack
- crawler/ : Node.js 20, ESM ("type":"module"), rss-parser, cheerio, node-cron, pg (Pool)
- web/     : Spring Boot 3, JPA, Thymeleaf, SB Admin 2 (Bootstrap 4.6), Chart.js 2.9
- DB       : PostgreSQL 16 (schema in crawler/schema.sql)

## Source
- Sources: Zenn, ITmedia, ASCII.jp (RSS) and Hatena IT hot entries (HTML); see crawler/seed.sql and the README table.
- source.type supports RSS and HTML. interval_min must be at least 10 minutes.

## Rules
- The schema is owned by crawler/schema.sql; Spring uses ddl-auto=validate.
- Use TIMESTAMPTZ in SQL and OffsetDateTime in Java.
- Always use parameterized SQL ($1, $2); never concatenate strings.
- One request per source per tick, 1s sleep between sources, custom User-Agent.
- Never commit .env; keep .env.example up to date.

## Requirements
- C1 RSS fetch, C2 HTML fetch, C3 UPSERT on url, C4 1-minute tick + per-source interval_min,
  C5 crawl_run history, C6 no overlapping ticks, C7 `npm run once`.
- D1 summary cards, D2 article list, D3 hourly new-article line chart, D4 keyword TOP 10 bar chart,
  D5 crawl history + failures, D6 source on/off, D7 auto refresh (60s).

## schema.sql
See crawler/schema.sql.
