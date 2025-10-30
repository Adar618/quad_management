package com.quadmgmt.domain.service;
import com.quadmgmt.domain.model.User; import com.quadmgmt.domain.model.enums.Role;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.test.context.junit.jupiter.SpringExtension; import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @ExtendWith(SpringExtension.class) @Transactional
class UserServiceTests {
  @Autowired UserService userService;
  @Test void createAndFetchUser() { User u=userService.createUser("tester","tester@example.com","pw", Role.OPERATOR); assertNotNull(u.getId()); User fetched=userService.getById(u.getId()); assertEquals("tester", fetched.getUsername()); assertEquals(Role.OPERATOR, fetched.getRole()); }
}
