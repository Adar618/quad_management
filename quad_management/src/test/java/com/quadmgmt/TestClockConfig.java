package com.quadmgmt;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Primary;
import java.time.Clock; import java.time.Instant; import java.time.ZoneId;
@TestConfiguration public class TestClockConfig {
  @Bean @Primary public Clock testClock() { return Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneId.systemDefault()); }
}
