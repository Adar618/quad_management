package com.quadmgmt.domain.service.impl;

import com.quadmgmt.domain.model.Booking;
import com.quadmgmt.domain.model.QuadBike;
import com.quadmgmt.domain.model.User;
import com.quadmgmt.domain.model.enums.BookingStatus;
import com.quadmgmt.domain.service.BookingService;
import com.quadmgmt.persistence.repository.BookingRepository;
import com.quadmgmt.persistence.repository.QuadBikeRepository;
import com.quadmgmt.persistence.repository.UserRepository;
import com.quadmgmt.web.dto.BookingDto;
import com.quadmgmt.web.mapper.ApiMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final QuadBikeRepository quadRepo;
    private final UserRepository userRepo;
    private final Clock clock;

    public BookingServiceImpl(BookingRepository b, QuadBikeRepository q, UserRepository u, Clock c) {
        this.bookingRepo = b; this.quadRepo = q; this.userRepo = u; this.clock = c;
    }

    @Override
    public Booking createBooking(Long userId, int numberOfQuads, LocalDateTime start, LocalDateTime end) {
        validate(userId, numberOfQuads, start, end);
        var available = quadRepo.findAvailableForRange(start, end, PageRequest.of(0, Math.max(1, numberOfQuads)));
        if (available.size() < numberOfQuads)
            throw new IllegalStateException("Insufficient inventory for requested time range");
        User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Booking b = new Booking();
        b.setUser(user);
        b.setNumberOfQuads(numberOfQuads);
        b.setStartTime(start);
        b.setEndTime(end);
        b.setTotalHours((int) Duration.between(start, end).toHours());
        b.setStatus(BookingStatus.PENDING);
        b.setQuads(new ArrayList<>(available.subList(0, numberOfQuads)));
        return bookingRepo.save(b);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (b.getStatus() == BookingStatus.CANCELLED) return; // idempotent
        if (b.getStatus() == BookingStatus.COMPLETED)
            throw new IllegalStateException("Cannot cancel a completed booking");
        b.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(b);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(int n, LocalDateTime s, LocalDateTime e) {
        return quadRepo.countAvailableForRange(s, e) >= n;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> listForUser(Long userId) {
        return bookingRepo.findByUser_IdOrderByStartTimeDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getById(Long bookingId) {
        return bookingRepo.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    @Override
    @Transactional
    public BookingDto cancelAndReturnDto(Long bookingId) {
        cancelBooking(bookingId);
        Booking updated = getById(bookingId);
        return ApiMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getByIdAsDto(Long bookingId) {
        return ApiMapper.toDto(getById(bookingId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> listForUserAsDto(Long userId) {
        return listForUser(userId).stream().map(ApiMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto cancelByActor(Long bookingId, Long actorId, boolean isAdmin) {
        Booking b = getById(bookingId);
        if (!isAdmin) {
            if (!b.getUserId().equals(actorId)) {
                throw new AccessDeniedException("Cannot cancel another user's booking");
            }
            // not started yet
            LocalDateTime now = LocalDateTime.now(clock);
            if (!now.isBefore(b.getStartTime())) {
                throw new IllegalStateException("Cannot cancel a booking that has started");
            }
        }
        cancelBooking(bookingId);
        return ApiMapper.toDto(getById(bookingId));
    }

    private void validate(Long userId, int n, LocalDateTime s, LocalDateTime e) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (n <= 0) throw new IllegalArgumentException("numberOfQuads must be positive");
        if (s == null || e == null) throw new IllegalArgumentException("start and end required");
        if (!e.isAfter(s)) throw new IllegalArgumentException("end must be after start");
        LocalDateTime now = LocalDateTime.now(clock);
        if (!s.isAfter(now)) throw new IllegalArgumentException("Cannot book in the past");
        long hours = java.time.Duration.between(s, e).toHours();
        if (hours < 1 || hours > 24) throw new IllegalArgumentException("Booking must be 1–24 hours long");
    }
}
