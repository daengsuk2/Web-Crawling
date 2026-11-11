package kr.ac.ync.radar.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Source {

	public static final int MIN_INTERVAL = 10; // ethics checklist: at least 10 minutes per source
	public static final int MAX_INTERVAL = 1440;

	@Id
	private Long id; // INSERT is done by the Node crawler

	private String name;
	private String type;
	private String url;
	private String listSelector;
	private boolean enabled;
	private int intervalMin;
	private OffsetDateTime lastCrawledAt;

	// D6: on/off and interval are the only columns the dashboard writes
	public void toggleEnabled() {
		this.enabled = !this.enabled;
	}

	public void changeInterval(int minutes) {
		if (minutes < MIN_INTERVAL || minutes > MAX_INTERVAL) {
			throw new IllegalArgumentException(
					"수집 주기는 " + MIN_INTERVAL + "분 이상 " + MAX_INTERVAL + "분 이하여야 합니다.");
		}
		this.intervalMin = minutes;
	}
}
