
package com.quadmgmt.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quadmgmt.domain.model.enums.QuadStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "quad_bikes")
public class QuadBike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="registration_number", unique = true, nullable=false, length=50) private String registrationNumber;
    @Column(nullable=false, length=100) private String model;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private QuadStatus status = QuadStatus.AVAILABLE;

    // Prevent recursion during Booking -> QuadBike -> Booking ...
    @JsonIgnore
    @ManyToMany(mappedBy = "quads") private List<Booking> bookings;

    @Column(name="created_at", updatable=false) private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId() {return id;} public void setId(Long id) {this.id=id;}
    public String getRegistrationNumber() {return registrationNumber;} public void setRegistrationNumber(String v) {registrationNumber=v;}
    public String getModel() {return model;} public void setModel(String v) {model=v;}
    public QuadStatus getStatus() {return status;} public void setStatus(QuadStatus v) {status=v;}
    public List<Booking> getBookings() {return bookings;} public void setBookings(List<Booking> v) {bookings=v;}
    public LocalDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(LocalDateTime v) {createdAt=v;}
    public LocalDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(LocalDateTime v) {updatedAt=v;}
}
