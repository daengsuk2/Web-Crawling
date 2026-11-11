package kr.ac.ync.radar.repository;

import kr.ac.ync.radar.entity.TrackedKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackedKeywordRepository extends JpaRepository<TrackedKeyword, Long> {
}
