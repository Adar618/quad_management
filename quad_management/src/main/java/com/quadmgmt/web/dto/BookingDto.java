
package com.quadmgmt.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingDto(
        Long id,
        Long userId,
        Integer numberOfQuads,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer totalHours,
        String status,
        List<QuadBikeDto> quads,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
