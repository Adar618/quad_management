package com.quadmgmt.domain.service;
import com.quadmgmt.domain.model.User;
import com.quadmgmt.domain.model.enums.Role;
import java.util.List;
public interface UserService {
  User createUser(String username, String email, String password, Role role);
  User getById(Long id);
  List<User> listAll();
}
