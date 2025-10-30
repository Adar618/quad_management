package com.quadmgmt;

import com.quadmgmt.domain.model.QuadBike;
import com.quadmgmt.domain.model.enums.QuadStatus;
import com.quadmgmt.domain.service.QuadBikeService;
import com.quadmgmt.web.dto.QuadBikeDto;
import com.quadmgmt.web.mapper.ApiMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/quads")
public class QuadController {
    private final QuadBikeService quads;
    public QuadController(QuadBikeService quads) { this.quads = quads; }

    public record CreateQuadRequest(String registrationNumber, String model) {}
    public record StatusRequest(QuadStatus status) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public QuadBikeDto create(@RequestBody CreateQuadRequest req) {
        QuadBike q = quads.create(req.registrationNumber(), req.model());
        return ApiMapper.toDto(q);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public QuadBikeDto get(@PathVariable Long id) { return ApiMapper.toDto(quads.getById(id)); }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public List<QuadBikeDto> list(@RequestParam(required = false) QuadStatus status) {
        return (status == null ? quads.listAll() : quads.listByStatus(status))
                .stream().map(ApiMapper::toDto).collect(Collectors.toList());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public QuadBikeDto updateStatus(@PathVariable Long id, @RequestBody StatusRequest req) {
        return ApiMapper.toDto(quads.updateStatus(id, req.status()));
    }

    @GetMapping("/availability")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public List<QuadBikeDto> available(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "10") int limit) {
        return quads.findAvailable(start, end, limit).stream().map(ApiMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/availability/count")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public long availableCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return quads.countAvailable(start, end);
    }
}
