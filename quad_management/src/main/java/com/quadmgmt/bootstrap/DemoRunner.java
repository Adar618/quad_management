package com.quadmgmt.bootstrap;
import com.quadmgmt.domain.model.Booking;
import com.quadmgmt.domain.model.User;
import com.quadmgmt.domain.service.BookingService;
import com.quadmgmt.domain.service.UserService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import java.time.LocalDateTime; import java.util.List;


public class DemoRunner implements CommandLineRunner {
  private static final Logger log = LoggerFactory.getLogger(DemoRunner.class);
  private final BookingService bookingService; private final UserService userService;
  public DemoRunner(BookingService b, UserService u) { this.bookingService=b; this.userService=u; }
  public void run(String... args) {
    List<User> users = userService.listAll();
    if (users.isEmpty()) return;
    User any = users.stream().filter(u->"op1".equals(u.getUsername())).findFirst().orElse(users.get(0));
    LocalDateTime start = LocalDateTime.now().plusHours(2); LocalDateTime end = start.plusHours(2);
    boolean ok = bookingService.isAvailable(2, start, end);
    log.info("Availability for 2 quads between {} and {} => {}", start, end, ok);
    if (ok) { Booking b1 = bookingService.createBooking(any.getId(), 2, start, end); log.info("Created booking id={} for user id={}", b1.getId(), any.getId()); }
    try { bookingService.createBooking(any.getId(), 10, start.plusMinutes(30), end.minusMinutes(30)); log.warn("Unexpected: conflicting booking succeeded"); }
    catch (Exception ex) { log.info("Expected failure for conflicting/insufficient booking: {}", ex.getMessage()); }
  }
}
