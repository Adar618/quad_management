package com.quadmgmt;

import com.quadmgmt.domain.model.Booking;
import com.quadmgmt.domain.service.BookingService;
import com.quadmgmt.security.UserPrincipal;
import com.quadmgmt.web.dto.BookingDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookingController {
    private final BookingService bookings;
    public BookingController(BookingService bookings) { this.bookings = bookings; }

    public record CreateBookingRequest(Long userId, int numberOfQuads,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {}
    public record Availability(boolean available) {}

    @GetMapping("/bookings/availability")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Availability check(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam("numberOfQuads") int n) {
        return new Availability(bookings.isAvailable(n, start, end));
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public BookingDto create(@RequestBody CreateBookingRequest req, @AuthenticationPrincipal UserPrincipal principal) {
        Long userIdToUse = req.userId();
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            userIdToUse = principal.getId();
        }
        Booking b = bookings.createBooking(userIdToUse, req.numberOfQuads(), req.startTime(), req.endTime());
        return com.quadmgmt.web.mapper.ApiMapper.toDto(b);
    }

    @PostMapping("/bookings/{bookingId}:cancel")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public BookingDto cancel(@PathVariable Long bookingId, @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return bookings.cancelByActor(bookingId, principal.getId(), isAdmin);
    }

    @GetMapping("/users/{userId}/bookings")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    public List<BookingDto> forUser(@PathVariable Long userId) {
        return bookings.listForUserAsDto(userId);
    }
}
