package com.quadmgmt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminRBACTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private String loginAdmin() throws Exception {
        String body = om.writeValueAsString(Map.of("username", "admin", "password", "secret"));
        String json = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return om.readTree(json).get("accessToken").asText();
    }

    @Test
    void admin_can_manage_users_and_quads_and_bookings() throws Exception {
        String token = loginAdmin();

        // Create user (ADMIN)
        String createUserBody = om.writeValueAsString(Map.of(
                "username", "op2",
                "email", "op2@example.com",
                "password", "secret",
                "role", "OPERATOR"
        ));
        mvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserBody))
                .andExpect(status().isCreated());

        // List users
        mvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Create a quad
        String createQuadBody = om.writeValueAsString(Map.of(
                "registrationNumber", "QUAD-77",
                "model", "Yamaha Raptor 77"
        ));
        mvc.perform(post("/api/v1/quads")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createQuadBody))
                .andExpect(status().isCreated());

        // Update quad status
        String statusBody = om.writeValueAsString(Map.of("status", "MAINTENANCE"));
        mvc.perform(patch("/api/v1/quads/1/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody))
                .andExpect(status().isOk());

        // Check availability
        mvc.perform(get("/api/v1/bookings/availability")
                        .header("Authorization", "Bearer " + token)
                        .param("start", "2025-11-01T14:00:00")
                        .param("end", "2025-11-01T16:00:00")
                        .param("numberOfQuads", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").isBoolean());

        // Create booking
        String createBookingBody = om.writeValueAsString(Map.of(
                "userId", 2,
                "numberOfQuads", 2,
                "startTime", "2025-11-01T14:00:00",
                "endTime", "2025-11-01T16:00:00"
        ));
        String bookingJson = mvc.perform(post("/api/v1/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long bookingId = om.readTree(bookingJson).get("id").asLong();

        // Cancel booking
        mvc.perform(post("/api/v1/bookings/" + bookingId + ":cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}