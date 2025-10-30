package com.quadmgmt;

import com.quadmgmt.persistence.repository.UserRepository;
import com.quadmgmt.security.UserPrincipal;
import com.quadmgmt.web.dto.UserDto;
import com.quadmgmt.web.mapper.ApiMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MeController {
    private final UserRepository users;

    public MeController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal UserPrincipal principal) {
        return users.findById(principal.getId())
                .map(ApiMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
