package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Find a user by their unique email address.
   *
   * @param email the email to search for
   * @return an Optional containing the User if found
   */
  Optional<User> findByEmail(String email);

  /**
   * Check if a user exists by their email address.
   *
   * @param email the email to check
   * @return true if a user with the given email exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Find a user by their confirmation token.
   *
   * @param token the confirmation token to search for
   * @return an Optional containing the User if found
   */
  Optional<User> findByConfirmationToken(String token);
}