-- Zenn /feed is a trending list (~5 new articles a day); the others publish dozens a day
INSERT INTO source (name, type, url, list_selector, enabled, interval_min) VALUES
    ('Zenn',     'RSS', 'https://zenn.dev/feed',                                  NULL, TRUE, 30),
    ('ITmedia',  'RSS', 'https://rss.itmedia.co.jp/rss/2.0/itmedia_all.xml',      NULL, TRUE, 20),
    ('ASCII.jp', 'RSS', 'https://ascii.jp/rss.xml',                               NULL, TRUE, 20)
ON CONFLICT (url) DO NOTHING;

-- HTML source (C2): list page scraped with a CSS selector, links resolved to absolute URLs
INSERT INTO source (name, type, url, list_selector, enabled, interval_min)
VALUES ('はてブ 人気(HTML)', 'HTML', 'https://b.hatena.ne.jp/hotentry/it',
        'h3.entrylist-contents-title > a', TRUE, 60)
ON CONFLICT (url) DO NOTHING;

INSERT INTO tracked_keyword (word) VALUES
    ('AI'), ('Python'), ('Rust'), ('TypeScript'), ('React'),
    ('Docker'), ('AWS'), ('Go'), ('セキュリティ'), ('LLM')
ON CONFLICT (word) DO NOTHING;
