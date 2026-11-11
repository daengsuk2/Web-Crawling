package kr.ac.ync.radar.repository;

import java.time.OffsetDateTime;
import java.util.List;
import kr.ac.ync.radar.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SourceRepository extends JpaRepository<Source, Long> {

	List<Source> findAllByOrderByIdAsc();

	long countByEnabledTrue();

	@Query("select max(s.lastCrawledAt) from Source s")
	OffsetDateTime latestCrawl();

	// shortest interval among sources the crawler still visits (D1 stalled-crawler check)
	@Query("select min(s.intervalMin) from Source s where s.enabled = true")
	Integer shortestActiveInterval();
}
