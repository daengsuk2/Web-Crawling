package kr.ac.ync.radar.repository;

import java.util.List;
import kr.ac.ync.radar.entity.TrackedKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrackedKeywordRepository extends JpaRepository<TrackedKeyword, Long> {

	boolean existsByWordIgnoreCase(String word);

	// keywords page: every keyword with its matches in the last 7 days
	@Query(value = """
			SELECT k.id AS id, k.word AS word, count(a.id) AS cnt
			FROM tracked_keyword k
			LEFT JOIN article a
			  ON a.title ILIKE '%' || k.word || '%'
			 AND a.first_seen_at >= now() - interval '7 days'
			GROUP BY k.id, k.word
			ORDER BY cnt DESC, k.word
			""", nativeQuery = true)
	List<KeywordCount> findAllWithCounts();
}
