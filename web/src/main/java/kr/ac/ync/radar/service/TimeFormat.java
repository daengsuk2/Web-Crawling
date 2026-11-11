package kr.ac.ync.radar.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

// Templates call this as ${@timeFmt.format(value, 'pattern')} so every time is shown in KST
@Component("timeFmt")
public class TimeFormat {

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	public String format(OffsetDateTime value, String pattern) {
		if (value == null) {
			return "-";
		}
		return value.atZoneSameInstant(KST).format(DateTimeFormatter.ofPattern(pattern));
	}

	// run duration such as "1.2초"; "-" while the run is still going
	public String seconds(OffsetDateTime start, OffsetDateTime end) {
		if (start == null || end == null) {
			return "-";
		}
		return String.format("%.1f초", Duration.between(start, end).toMillis() / 1000.0);
	}
}
