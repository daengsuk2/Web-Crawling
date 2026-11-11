package kr.ac.ync.radar.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tracked_keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackedKeyword {

	@Id
	private Long id;

	private String word;
	private OffsetDateTime createdAt;
}
