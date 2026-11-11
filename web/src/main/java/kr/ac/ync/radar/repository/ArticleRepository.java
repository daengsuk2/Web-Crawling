package kr.ac.ync.radar.repository;

import java.util.List;
import kr.ac.ync.radar.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	// D2: newest first with title search and source filter; pattern is already lower-cased and escaped with '!'.
	// The source is fetched in the same query (open-in-view is off).
	@Query(value = """
			select a from Article a join fetch a.source s
			where lower(a.title) like :pattern escape '!'
			  and (:sourceId is null or s.id = :sourceId)
			order by a.firstSeenAt desc
			""",
			countQuery = """
			select count(a) from Article a
			where lower(a.title) like :pattern escape '!'
			  and (:sourceId is null or a.source.id = :sourceId)
			""")
	Page<Article> search(@Param("pattern") String pattern, @Param("sourceId") Long sourceId, Pageable pageable);

	// D1: articles first seen since midnight (Asia/Seoul)
	@Query(value = """
			SELECT count(*) FROM article
			WHERE first_seen_at >= date_trunc('day', now() AT TIME ZONE 'Asia/Seoul') AT TIME ZONE 'Asia/Seoul'
			""", nativeQuery = true)
	long countToday();

	// D3: new articles per hour, last 24 hours
	@Query(value = """
			SELECT to_char(date_trunc('hour', first_seen_at AT TIME ZONE 'Asia/Seoul'), 'MM-DD HH24:00') AS label,
			       count(*) AS cnt
			FROM article
			WHERE first_seen_at >= now() - interval '24 hours'
			GROUP BY 1 ORDER BY 1
			""", nativeQuery = true)
	List<LabelCount> hourlyNew();

	// D4: keyword TOP 10, matched in titles of the last 7 days
	@Query(value = """
			SELECT k.word AS label, count(a.id) AS cnt
			FROM tracked_keyword k
			LEFT JOIN article a
			  ON a.title ILIKE '%' || k.word || '%'
			 AND a.first_seen_at >= now() - interval '7 days'
			GROUP BY k.word
			ORDER BY cnt DESC, k.word
			LIMIT 10
			""", nativeQuery = true)
	List<LabelCount> keywordTop();
}
