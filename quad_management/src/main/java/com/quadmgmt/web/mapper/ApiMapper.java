
package com.quadmgmt.web.mapper;

import com.quadmgmt.domain.model.Booking;
import com.quadmgmt.domain.model.QuadBike;
import com.quadmgmt.domain.model.User;
import com.quadmgmt.web.dto.BookingDto;
import com.quadmgmt.web.dto.QuadBikeDto;
import com.quadmgmt.web.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public final class ApiMapper {
    private ApiMapper() {}

    public static UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name());
    }

    public static QuadBikeDto toDto(QuadBike q) {
        return new QuadBikeDto(q.getId(), q.getRegistrationNumber(), q.getModel(), q.getStatus().name());
    }

    public static BookingDto toDto(Booking b) {
        List<QuadBikeDto> quadDtos = b.getQuads() == null ? List.of() : b.getQuads().stream().map(ApiMapper::toDto).collect(Collectors.toList());
        return new BookingDto(
                b.getId(),
                b.getUserId(),
                b.getNumberOfQuads(),
                b.getStartTime(),
                b.getEndTime(),
                b.getTotalHours(),
                b.getStatus().name(),
                quadDtos,
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }
}
