# YNC Tech Radar

Crawls Japanese IT articles (Zenn RSS) into PostgreSQL and shows them on a Spring Boot dashboard.

## Run (week 1: crawler)
```bash
docker run -d --name radar-db -e POSTGRES_USER=radar -e POSTGRES_PASSWORD=radar1234 \
  -e POSTGRES_DB=radar -p 5432:5432 postgres:16
docker exec -i radar-db psql -U radar -d radar < crawler/schema.sql
docker exec -i radar-db psql -U radar -d radar < crawler/seed.sql

cd crawler
cp .env.example .env
npm install
npm run once
```

## Requirements checklist
TODO (C1~C7, D1~D7, R1~R8)

## AI usage
See docs/prompt-log.md.
