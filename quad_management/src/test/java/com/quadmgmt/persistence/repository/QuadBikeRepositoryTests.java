package com.quadmgmt.persistence.repository;
import com.quadmgmt.TestClockConfig; import com.quadmgmt.domain.model.Booking; import com.quadmgmt.domain.model.QuadBike; import com.quadmgmt.domain.model.enums.BookingStatus; import com.quadmgmt.domain.model.enums.QuadStatus; import com.quadmgmt.domain.service.BookingService;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.context.annotation.Import; import org.springframework.data.domain.PageRequest; import org.springframework.test.context.junit.jupiter.SpringExtension; import org.springframework.transaction.annotation.Transactional;
import java.time.Clock; import java.time.LocalDateTime; import java.util.List; import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @ExtendWith(SpringExtension.class) @Import(TestClockConfig.class) @Transactional
class QuadBikeRepositoryTests {
  @Autowired QuadBikeRepository quadBikeRepository; @Autowired BookingRepository bookingRepository; @Autowired BookingService bookingService; @Autowired Clock clock;
  private LocalDateTime baseStart() { return LocalDateTime.now(clock).plusHours(2); }
  @Test void availableQuery_allowsBackToBack() { var start=baseStart(); var end=start.plusHours(2); bookingService.createBooking(2L,4,start,end); List<QuadBike> atBoundary=quadBikeRepository.findAvailableForRange(end, end.plusHours(2), PageRequest.of(0,10)); assertEquals(10, atBoundary.size()); }
  @Test void cancelledBookings_doNotBlock() { var start=baseStart(); var end=start.plusHours(2); Booking b=bookingService.createBooking(2L,5,start,end); b.setStatus(BookingStatus.CANCELLED); bookingRepository.save(b); long count=quadBikeRepository.countAvailableForRange(start.plusMinutes(10), end.minusMinutes(10)); assertEquals(10, count); }
  @Test void maintenanceQuads_areExcluded() { List<QuadBike> all=quadBikeRepository.findAll(); for (int i=0;i<3;i++){ var q=all.get(i); q.setStatus(QuadStatus.MAINTENANCE); quadBikeRepository.save(q);} var start=baseStart(); var end=start.plusHours(1); long count=quadBikeRepository.countAvailableForRange(start,end); assertEquals(7, count); }
}
