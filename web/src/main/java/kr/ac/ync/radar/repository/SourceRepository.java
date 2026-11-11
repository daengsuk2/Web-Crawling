package kr.ac.ync.radar.repository;

import java.util.List;
import kr.ac.ync.radar.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceRepository extends JpaRepository<Source, Long> {

	List<Source> findAllByOrderByIdAsc();

	long countByEnabledTrue();
}
