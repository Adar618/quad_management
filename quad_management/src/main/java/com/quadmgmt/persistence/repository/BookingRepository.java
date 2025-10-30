
package com.quadmgmt.persistence.repository;

import com.quadmgmt.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Use property path navigation to access the user.id field
    List<Booking> findByUser_IdOrderByStartTimeDesc(Long userId);

    // Optional alternative using JPQL (kept for reference)
    // @Query("select b from Booking b where b.user.id = :userId order by b.startTime desc")
    // List<Booking> findAllByUser(@Param("userId") Long userId);
}
