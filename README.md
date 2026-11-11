# YNC Tech Radar

Crawls Japanese IT articles (Zenn RSS) into PostgreSQL and shows them on a Spring Boot dashboard.

## Structure
```
crawler/   Node.js crawler (src/, schema.sql, seed.sql, .env.example)
web/       Spring Boot dashboard (week 2)
docs/      context.md, prompt-log.md, screenshots/
```

## Run (week 1: crawler)
```bash
docker run -d --name radar-db -e POSTGRES_USER=radar -e POSTGRES_PASSWORD=radar1234 \
  -e POSTGRES_DB=radar -p 5432:5432 postgres:16
docker exec -i radar-db psql -U radar -d radar < crawler/schema.sql
docker exec -i radar-db psql -U radar -d radar < crawler/seed.sql

cd crawler
cp .env.example .env
npm install
npm run once   # crawl every enabled source once
npm start      # scheduler: tick every minute, per-source interval_min
```

## Data source
- Zenn RSS `https://zenn.dev/feed` (robots.txt only disallows `/search`).
- Request rules: custom User-Agent, 10s timeout, 1s sleep between sources, `interval_min` 30.

## Requirements checklist
### Crawler (week 1)
- [x] C1 RSS fetch (`fetchers.js`)
- [x] C2 HTML fetch with relative URL resolution (`fetchers.js`, not used by the current source)
- [x] C3 UPSERT on `url` (`repo.js`)
- [x] C4 1-minute tick + per-source `interval_min` (`index.js`)
- [x] C5 `crawl_run` history (`repo.js`, `crawl.js`)
- [x] C6 no overlapping ticks (`index.js`)
- [x] C7 `npm run once`

### Rules
- [x] R1 new url -> INSERT
- [x] R2 changed content -> `updated_at`
- [x] R3 unchanged -> `last_seen_at` only
- [x] R4 only sources past `interval_min`
- [x] R5 `enabled = false` is skipped
- [x] R6 no overlap while a tick is running
- [x] R7 failure -> FAILED, other sources continue
- [x] R8 1s sleep between sources, `last_crawled_at` updated in `finally`

### Dashboard (week 2)
- [ ] D1 summary cards
- [ ] D2 article list
- [ ] D3 hourly new-article line chart
- [ ] D4 keyword TOP 10 bar chart
- [ ] D5 crawl history + failures
- [ ] D6 source on/off
- [ ] D7 auto refresh (60s)

## AI usage
See [docs/prompt-log.md](docs/prompt-log.md).
