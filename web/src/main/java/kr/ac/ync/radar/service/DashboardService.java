package kr.ac.ync.radar.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import kr.ac.ync.radar.entity.Article;
import kr.ac.ync.radar.entity.CrawlRun;
import kr.ac.ync.radar.entity.Source;
import kr.ac.ync.radar.entity.TrackedKeyword;
import kr.ac.ync.radar.repository.ArticleRepository;
import kr.ac.ync.radar.repository.CrawlRunRepository;
import kr.ac.ync.radar.repository.KeywordCount;
import kr.ac.ync.radar.repository.LabelCount;
import kr.ac.ync.radar.repository.SourceRepository;
import kr.ac.ync.radar.repository.TrackedKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private static final int RECENT_RUNS = 10;
	private static final int MAX_KEYWORD_LENGTH = 50;

	private final ArticleRepository articles;
	private final CrawlRunRepository runs;
	private final SourceRepository sources;
	private final TrackedKeywordRepository keywords;

	/** lastCrawl is text such as "3분 전"; stalled means the crawler seems to be down. */
	public record Summary(long totalArticles, long todayNew, long enabledSources, String lastCrawl, boolean stalled) {
	}

	public record Chart(List<String> labels, List<Long> values) {
	}

	public Summary summary() {
		OffsetDateTime last = sources.latestCrawl();
		Integer shortest = sources.shortestActiveInterval();
		Long minutesAgo = last == null ? null : Duration.between(last, OffsetDateTime.now()).toMinutes();
		// D1: no crawl for more than twice the shortest interval -> "crawler stopped?"
		boolean stalled = shortest != null && (minutesAgo == null || minutesAgo > 2L * shortest);
		return new Summary(
				articles.count(),
				articles.countToday(),
				sources.countByEnabledTrue(),
				ago(minutesAgo),
				stalled);
	}

	public Chart hourlyNew() {
		return toChart(articles.hourlyNew());
	}

	public Chart keywordTop() {
		return toChart(articles.keywordTop());
	}

	/** D2: title search (case-insensitive, partial match) and optional source filter. */
	public Page<Article> searchArticles(String query, Long sourceId, int page, int size) {
		return articles.search(likePattern(query), sourceId, PageRequest.of(Math.max(page, 0), size));
	}

	public List<CrawlRun> recentRuns() {
		return runs.findRecent(PageRequest.of(0, RECENT_RUNS));
	}

	public Page<CrawlRun> runPage(int page, int size) {
		return runs.findPage(PageRequest.of(Math.max(page, 0), size));
	}

	public List<Source> allSources() {
		return sources.findAllByOrderByIdAsc();
	}

	public List<KeywordCount> keywordCounts() {
		return keywords.findAllWithCounts();
	}

	// D6: dirty checking persists the change; the crawler reads it on its next tick (R5)
	@Transactional
	public void toggleSource(Long id) {
		sources.findById(id).ifPresent(Source::toggleEnabled);
	}

	/** D6: throws IllegalArgumentException when the interval is outside 10..1440 minutes. */
	@Transactional
	public void changeInterval(Long id, int minutes) {
		sources.findById(id).ifPresent(s -> s.changeInterval(minutes));
	}

	/** D4: returns an error message, or null when the keyword was added. */
	@Transactional
	public String addKeyword(String raw) {
		String word = raw == null ? "" : raw.trim();
		if (word.isEmpty()) {
			return "키워드를 입력해 주세요.";
		}
		if (word.length() > MAX_KEYWORD_LENGTH) {
			return "키워드는 " + MAX_KEYWORD_LENGTH + "자 이하여야 합니다.";
		}
		if (keywords.existsByWordIgnoreCase(word)) {
			return "이미 등록된 키워드입니다.";
		}
		keywords.save(new TrackedKeyword(word));
		return null;
	}

	@Transactional
	public void deleteKeyword(Long id) {
		keywords.deleteById(id);
	}

	// lower-cased "%text%"; '!' is the escape character so user input cannot act as a wildcard
	static String likePattern(String query) {
		if (query == null || query.isBlank()) {
			return "%";
		}
		String escaped = query.trim().toLowerCase()
				.replace("!", "!!").replace("%", "!%").replace("_", "!_");
		return "%" + escaped + "%";
	}

	private static String ago(Long minutes) {
		if (minutes == null) {
			return "기록 없음";
		}
		if (minutes < 1) {
			return "방금 전";
		}
		if (minutes < 60) {
			return minutes + "분 전";
		}
		if (minutes < 1440) {
			return minutes / 60 + "시간 전";
		}
		return minutes / 1440 + "일 전";
	}

	private Chart toChart(List<LabelCount> rows) {
		return new Chart(
				rows.stream().map(LabelCount::getLabel).toList(),
				rows.stream().map(LabelCount::getCnt).toList());
	}
}
