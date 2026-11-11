package kr.ac.ync.radar.repository;

import java.time.OffsetDateTime;
import java.util.List;
import kr.ac.ync.radar.entity.CrawlRun;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CrawlRunRepository extends JpaRepository<CrawlRun, Long> {

	@Query("select r from CrawlRun r join fetch r.source order by r.startedAt desc")
	List<CrawlRun> findRecent(Pageable pageable);

	long countByStatusAndStartedAtAfter(String status, OffsetDateTime after);
}
