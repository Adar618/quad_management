package com.quadmgmt.persistence.repository;
import com.quadmgmt.domain.model.QuadBike;
import com.quadmgmt.domain.model.enums.QuadStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
public interface QuadBikeRepository extends JpaRepository<QuadBike, Long> {
  List<QuadBike> findByStatus(QuadStatus status);
  boolean existsByRegistrationNumber(String registrationNumber);
  @Query("""
    SELECT q FROM QuadBike q
    WHERE q.status = 'AVAILABLE'
      AND q.id NOT IN (
        SELECT q2.id FROM Booking b JOIN b.quads q2
        WHERE b.status IN ('PENDING','CONFIRMED')
          AND (b.startTime < :endTime AND b.endTime > :startTime)
      )
    """)
  List<QuadBike> findAvailableForRange(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime,
                                       Pageable pageable);
  @Query("""
    SELECT COUNT(q) FROM QuadBike q
    WHERE q.status = 'AVAILABLE'
      AND q.id NOT IN (
        SELECT q2.id FROM Booking b JOIN b.quads q2
        WHERE b.status IN ('PENDING','CONFIRMED')
          AND (b.startTime < :endTime AND b.endTime > :startTime)
      )
    """)
  long countAvailableForRange(@Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);
}
