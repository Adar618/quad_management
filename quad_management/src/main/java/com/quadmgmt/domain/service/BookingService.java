package com.quadmgmt.domain.service;

import com.quadmgmt.domain.model.Booking;
import com.quadmgmt.web.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, int numberOfQuads, LocalDateTime start, LocalDateTime end);
    void cancelBooking(Long bookingId);
    boolean isAvailable(int numberOfQuads, LocalDateTime start, LocalDateTime end);
    List<Booking> listForUser(Long userId);
    Booking getById(Long bookingId);
    BookingDto cancelAndReturnDto(Long bookingId);
    BookingDto getByIdAsDto(Long bookingId);
    List<BookingDto> listForUserAsDto(Long userId);

    // New: enforce actor permissions
    BookingDto cancelByActor(Long bookingId, Long actorId, boolean isAdmin);
}
