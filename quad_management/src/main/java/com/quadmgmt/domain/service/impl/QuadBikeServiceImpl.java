
package com.quadmgmt.domain.service.impl;

import com.quadmgmt.domain.model.QuadBike;
import com.quadmgmt.domain.model.enums.QuadStatus;
import com.quadmgmt.domain.service.QuadBikeService;
import com.quadmgmt.persistence.repository.QuadBikeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class QuadBikeServiceImpl implements QuadBikeService {
    private final QuadBikeRepository quadRepo;
    public QuadBikeServiceImpl(QuadBikeRepository quadRepo) { this.quadRepo = quadRepo; }

    @Override
    public QuadBike create(String registrationNumber, String model) {
        if (quadRepo.existsByRegistrationNumber(registrationNumber))
            throw new IllegalStateException("Registration number already exists"); // -> 409 via handler
        QuadBike q = new QuadBike();
        q.setRegistrationNumber(registrationNumber);
        q.setModel(model);
        q.setStatus(QuadStatus.AVAILABLE);
        return quadRepo.save(q);
    }

    @Override
    @Transactional(readOnly = true)
    public QuadBike getById(Long id) { return quadRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Quad not found")); }

    @Override
    @Transactional(readOnly = true)
    public List<QuadBike> listAll() { return quadRepo.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public List<QuadBike> listByStatus(QuadStatus s) { return quadRepo.findByStatus(s); }

    @Override
    public QuadBike updateStatus(Long id, QuadStatus s) { QuadBike q = getById(id); q.setStatus(s); return quadRepo.save(q); }

    @Override
    @Transactional(readOnly = true)
    public List<QuadBike> findAvailable(LocalDateTime start, LocalDateTime end, int limit) { return quadRepo.findAvailableForRange(start, end, PageRequest.of(0, Math.max(1, limit))); }

    @Override
    @Transactional(readOnly = true)
    public long countAvailable(LocalDateTime start, LocalDateTime end) { return quadRepo.countAvailableForRange(start, end); }
}
