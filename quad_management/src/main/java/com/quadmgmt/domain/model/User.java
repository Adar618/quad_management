
package com.quadmgmt.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quadmgmt.domain.model.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 50) private String username;
    @Column(unique = true, nullable = false, length = 100) private String email;
    @Column(nullable = false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role = Role.OPERATOR;

    // Prevent recursion & lazy serialization issues
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }

    // getters/setters
    public Long getId() {return id;} public void setId(Long id) {this.id=id;}
    public String getUsername() {return username;} public void setUsername(String v) {username=v;}
    public String getEmail() {return email;} public void setEmail(String v) {email=v;}
    public String getPassword() {return password;} public void setPassword(String v) {password=v;}
    public Role getRole() {return role;} public void setRole(Role v) {role=v;}
    public List<Booking> getBookings() {return bookings;} public void setBookings(List<Booking> v) {bookings=v;}
    public LocalDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(LocalDateTime v) {createdAt=v;}
    public LocalDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(LocalDateTime v) {updatedAt=v;}
}
