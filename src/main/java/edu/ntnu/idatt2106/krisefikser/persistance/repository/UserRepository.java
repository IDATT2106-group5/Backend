package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  Optional<User> findByEmail(String email);
}

