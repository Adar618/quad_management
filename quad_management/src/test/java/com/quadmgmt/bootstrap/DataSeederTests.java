package com.quadmgmt.bootstrap;
import com.quadmgmt.persistence.repository.*; import org.junit.jupiter.api.*; import org.junit.jupiter.api.extension.ExtendWith; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.test.context.junit.jupiter.SpringExtension; import org.springframework.transaction.annotation.Transactional; import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @ExtendWith(SpringExtension.class) @Transactional
class DataSeederTests{
 @Autowired UserRepository userRepository; @Autowired QuadBikeRepository quadBikeRepository;
 @Test void seederCreatedUsersAndQuads(){ assertTrue(userRepository.count() >= 2); assertEquals(10, quadBikeRepository.count()); }
}
