
package com.quadmgmt.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quadmgmt.domain.model.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "bookings")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @JsonIgnore // prevent LAZY serialization issues when Open-Session-In-View is false
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false) private User user;

    @Column(name="number_of_quads", nullable=false) private Integer numberOfQuads;
    @Column(name="start_time", nullable=false) private LocalDateTime startTime;
    @Column(name="end_time", nullable=false) private LocalDateTime endTime;
    @Column(name="total_hours", nullable=false) private Integer totalHours;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private BookingStatus status = BookingStatus.PENDING;

    @JsonIgnoreProperties({"bookings"})
    @ManyToMany @JoinTable(name="booking_quads", joinColumns=@JoinColumn(name="booking_id"), inverseJoinColumns=@JoinColumn(name="quad_id"))
    private List<QuadBike> quads = new ArrayList<>();

    @Column(name="created_at", updatable=false) private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId() {return id;} public void setId(Long id) {this.id=id;}
    public User getUser() {return user;} public void setUser(User v) {user=v;}
    public Long getUserId() { return user != null ? user.getId() : null; }
    public Integer getNumberOfQuads() {return numberOfQuads;} public void setNumberOfQuads(Integer v) {numberOfQuads=v;}
    public LocalDateTime getStartTime() {return startTime;} public void setStartTime(LocalDateTime v) {startTime=v;}
    public LocalDateTime getEndTime() {return endTime;} public void setEndTime(LocalDateTime v) {endTime=v;}
    public Integer getTotalHours() {return totalHours;} public void setTotalHours(Integer v) {totalHours=v;}
    public BookingStatus getStatus() {return status;} public void setStatus(BookingStatus v) {status=v;}
    public List<QuadBike> getQuads() {return quads;} public void setQuads(List<QuadBike> v) {quads=v;}
    public LocalDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(LocalDateTime v) {createdAt=v;}
    public LocalDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(LocalDateTime v) {updatedAt=v;}
}
