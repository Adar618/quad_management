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
class OperatorRBACTests {
    
    @Autowired 
    private MockMvc mvc;
    
    @Autowired 
    private ObjectMapper om;

    private String loginOp() throws Exception {
        String body = om.writeValueAsString(
            Map.of("username", "op1", "password", "secret")
        );
        
        String json = mvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        return om.readTree(json).get("accessToken").asText();
    }

    @Test 
    void operator_perms_enforced() throws Exception {
        String token = loginOp();

        // Cannot create/list users
        String createUserBody = om.writeValueAsString(Map.of(
            "username", "x",
            "email", "x@ex.com",
            "password", "p",
            "role", "OPERATOR"
        ));
        
        mvc.perform(
                post("/api/v1/users")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createUserBody)
            )
            .andExpect(status().isForbidden());
        
        mvc.perform(
                get("/api/v1/users")
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isForbidden());

        // Can view own profile via /me
        mvc.perform(
                get("/api/v1/me")
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("op1"));

        // Can get own user by id (assumes op1 has id=2)
        mvc.perform(
                get("/api/v1/users/2")
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isOk());

        // Cannot get another user's profile (id=1 admin)
        mvc.perform(
                get("/api/v1/users/1")
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isForbidden());

        // Can list and get quads
        mvc.perform(
                get("/api/v1/quads")
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isOk());

        // Cannot create/update quads
        String createQuadBody = om.writeValueAsString(Map.of(
            "registrationNumber", "QUAD-88",
            "model", "Yamaha Raptor 88"
        ));
        
        mvc.perform(
                post("/api/v1/quads")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createQuadBody)
            )
            .andExpect(status().isForbidden());
        
        String patchStatusBody = om.writeValueAsString(
            Map.of("status", "MAINTENANCE")
        );
        
        mvc.perform(
                patch("/api/v1/quads/1/status")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(patchStatusBody)
            )
            .andExpect(status().isForbidden());

        // Can check availability
        mvc.perform(
                get("/api/v1/bookings/availability")
                    .header("Authorization", "Bearer " + token)
                    .param("start", "2025-11-01T14:00:00")
                    .param("end", "2025-11-01T16:00:00")
                    .param("numberOfQuads", "1")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available").isBoolean());

        // Can create a booking (userId in body will be ignored and set to self)
        String createBookingBody = om.writeValueAsString(Map.of(
            "userId", 1,
            "numberOfQuads", 1,
            "startTime", "2025-11-01T14:00:00",
            "endTime", "2025-11-01T16:00:00"
        ));
        
        String created = mvc.perform(
                post("/api/v1/bookings")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createBookingBody)
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        long bookingId = om.readTree(created).get("id").asLong();

        // Can cancel own booking (before start)
        mvc.perform(
                post("/api/v1/bookings/" + bookingId + ":cancel")
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}