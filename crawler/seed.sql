INSERT INTO source (name, type, url, list_selector, enabled, interval_min)
VALUES ('Zenn', 'RSS', 'https://zenn.dev/feed', NULL, TRUE, 30)
ON CONFLICT (url) DO NOTHING;

INSERT INTO tracked_keyword (word) VALUES
    ('AI'), ('Python'), ('Rust'), ('TypeScript'), ('React'),
    ('Docker'), ('AWS'), ('Go'), ('セキュリティ'), ('LLM')
ON CONFLICT (word) DO NOTHING;
