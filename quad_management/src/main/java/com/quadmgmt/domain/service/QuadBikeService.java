package com.quadmgmt.domain.service;
import com.quadmgmt.domain.model.QuadBike;
import com.quadmgmt.domain.model.enums.QuadStatus;
import java.time.LocalDateTime;
import java.util.List;
public interface QuadBikeService {
  QuadBike create(String registrationNumber, String model);
  QuadBike getById(Long id);
  List<QuadBike> listAll();
  List<QuadBike> listByStatus(QuadStatus status);
  QuadBike updateStatus(Long id, QuadStatus status);
  List<QuadBike> findAvailable(LocalDateTime start, LocalDateTime end, int limit);
  long countAvailable(LocalDateTime start, LocalDateTime end);
}
