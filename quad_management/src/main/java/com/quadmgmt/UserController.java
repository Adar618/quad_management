package com.quadmgmt;

import com.quadmgmt.domain.model.User;
import com.quadmgmt.domain.model.enums.Role;
import com.quadmgmt.domain.service.UserService;
import com.quadmgmt.security.UserPrincipal;
import com.quadmgmt.web.dto.UserDto;
import com.quadmgmt.web.mapper.ApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService users;
    public UserController(UserService users) { this.users = users; }

    public record CreateUserRequest(String username, String email, String password, Role role) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto create(@RequestBody CreateUserRequest req) {
        User u = users.createUser(req.username(), req.email(), req.password(), req.role());
        return ApiMapper.toDto(u);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public UserDto get(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiMapper.toDto(users.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> list() {
        return users.listAll().stream().map(ApiMapper::toDto).collect(Collectors.toList());
    }
}
