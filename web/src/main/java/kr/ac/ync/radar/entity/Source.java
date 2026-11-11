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

	@Id
	private Long id; // INSERT is done by the Node crawler

	private String name;
	private String type;
	private String url;
	private String listSelector;
	private boolean enabled;
	private int intervalMin;
	private OffsetDateTime lastCrawledAt;

	// D6: the only write the dashboard performs
	public void toggleEnabled() {
		this.enabled = !this.enabled;
	}
}
