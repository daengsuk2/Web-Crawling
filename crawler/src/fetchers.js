import Parser from 'rss-parser';
import * as cheerio from 'cheerio';

const UA = 'YNC-TechRadar-Student/1.0 (edu)';
const TIMEOUT_MS = 10000;

const parser = new Parser();

// C1: RSS -> [{ url, title, summary, publishedAt }]
export async function fetchRss(url) {
  const res = await fetch(url, {
    headers: { 'User-Agent': UA },
    signal: AbortSignal.timeout(TIMEOUT_MS),
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const feed = await parser.parseString(await res.text());
  return feed.items
    .filter((it) => it.link && it.title)
    .map((it) => ({
      url: it.link,
      title: it.title.trim(),
      summary: it.contentSnippet?.slice(0, 300) ?? null,
      publishedAt: it.isoDate ? new Date(it.isoDate) : null,
    }));
}

// C2: HTML list page -> [{ url, title }]
export async function fetchHtml(url, selector) {
  const res = await fetch(url, {
    headers: { 'User-Agent': UA },
    signal: AbortSignal.timeout(TIMEOUT_MS),
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const $ = cheerio.load(await res.text());
  return $(selector)
    .map((_, el) => {
      const href = $(el).attr('href');
      if (!href) return null;
      return {
        url: new URL(href, url).href,
        title: ($(el).attr('title') ?? $(el).text()).trim(),
        summary: null,
        publishedAt: null,
      };
    })
    .get();
}
