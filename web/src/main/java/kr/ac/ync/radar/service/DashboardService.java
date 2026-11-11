package kr.ac.ync.radar.service;

import java.time.OffsetDateTime;
import java.util.List;
import kr.ac.ync.radar.entity.Article;
import kr.ac.ync.radar.entity.CrawlRun;
import kr.ac.ync.radar.entity.Source;
import kr.ac.ync.radar.repository.ArticleRepository;
import kr.ac.ync.radar.repository.CrawlRunRepository;
import kr.ac.ync.radar.repository.LabelCount;
import kr.ac.ync.radar.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private static final int RECENT_ARTICLES = 10;
	private static final int RECENT_RUNS = 10;

	private final ArticleRepository articles;
	private final CrawlRunRepository runs;
	private final SourceRepository sources;

	public record Summary(long totalArticles, long todayNew, long enabledSources, long failedRuns24h) {
	}

	public record Chart(List<String> labels, List<Long> values) {
	}

	public Summary summary() {
		return new Summary(
				articles.count(),
				articles.countToday(),
				sources.countByEnabledTrue(),
				runs.countByStatusAndStartedAtAfter("FAILED", OffsetDateTime.now().minusHours(24)));
	}

	public Chart hourlyNew() {
		return toChart(articles.hourlyNew());
	}

	public Chart keywordTop() {
		return toChart(articles.keywordTop());
	}

	public List<Article> recentArticles() {
		return articles.findLatest(PageRequest.of(0, RECENT_ARTICLES)).getContent();
	}

	public Page<Article> articlePage(int page, int size) {
		return articles.findLatest(PageRequest.of(Math.max(page, 0), size));
	}

	public List<CrawlRun> recentRuns() {
		return runs.findRecent(PageRequest.of(0, RECENT_RUNS));
	}

	public List<Source> allSources() {
		return sources.findAllByOrderByIdAsc();
	}

	// D6: dirty checking persists the flag; the crawler reads it on its next tick (R5)
	@Transactional
	public void toggleSource(Long id) {
		sources.findById(id).ifPresent(Source::toggleEnabled);
	}

	private Chart toChart(List<LabelCount> rows) {
		return new Chart(
				rows.stream().map(LabelCount::getLabel).toList(),
				rows.stream().map(LabelCount::getCnt).toList());
	}
}
