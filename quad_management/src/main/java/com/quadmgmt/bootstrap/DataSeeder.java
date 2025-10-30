package com.quadmgmt.bootstrap;
import com.quadmgmt.domain.model.User;
import com.quadmgmt.domain.model.enums.Role;
import com.quadmgmt.domain.service.QuadBikeService;
import com.quadmgmt.domain.service.UserService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments; import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
@Component
public class DataSeeder implements ApplicationRunner {
  private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
  private final UserService userService; private final QuadBikeService quadService;
  public DataSeeder(UserService u, QuadBikeService q) { this.userService=u; this.quadService=q; }
  public void run(ApplicationArguments args) {
    if (userService.listAll().isEmpty()) {
      User admin = userService.createUser("admin","admin@example.com","secret", Role.ADMIN);
      User op1 = userService.createUser("op1","op1@example.com","secret", Role.OPERATOR);
      log.info("Seeded users: admin={}, operator={}", admin.getId(), op1.getId());
    }
    if (quadService.listAll().isEmpty()) {
      for (int i=1;i<=10;i++) quadService.create("QUAD-"+i, "Yamaha Raptor "+i);
      log.info("Seeded 10 quad bikes");
    }
  }
}
