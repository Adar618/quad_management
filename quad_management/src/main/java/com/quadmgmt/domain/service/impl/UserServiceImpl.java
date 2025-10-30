package com.quadmgmt.domain.service.impl;

import com.quadmgmt.domain.model.User;
import com.quadmgmt.domain.model.enums.Role;
import com.quadmgmt.domain.service.UserService;
import com.quadmgmt.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo; this.encoder = encoder;
    }

    public User createUser(String username, String email, String password, Role role) {
        User u = new User(); u.setUsername(username); u.setEmail(email); u.setPassword(encoder.encode(password)); u.setRole(role);
        return repo.save(u);
    }

    @Transactional(readOnly = true) public User getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional(readOnly = true) public List<User> listAll() { return repo.findAll(); }
}
