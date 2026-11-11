package kr.ac.ync.radar.repository;

import java.util.List;
import kr.ac.ync.radar.entity.CrawlRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CrawlRunRepository extends JpaRepository<CrawlRun, Long> {

	@Query("select r from CrawlRun r join fetch r.source order by r.startedAt desc")
	List<CrawlRun> findRecent(Pageable pageable);

	@Query(value = "select r from CrawlRun r join fetch r.source order by r.startedAt desc",
			countQuery = "select count(r) from CrawlRun r")
	Page<CrawlRun> findPage(Pageable pageable);
}
