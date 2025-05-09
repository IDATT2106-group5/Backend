package edu.ntnu.idatt2106.krisefikser.service.user;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.user.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.user.UserRepository;
import edu.ntnu.idatt2106.krisefikser.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring Security's UserDetailsService to load user-specific data. It uses the
 * UserRepository to find users and wraps them in CustomUserDetails.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
  private final UserRepository userRepository;

  /**
   * Constructor for CustomUserDetailsService.
   *
   * @param userRepository the UserRepository to access user data
   */
  
  @Autowired
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
    logger.info("CustomUserDetailsService initialized");
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.info("Loading user details for username: {}", username);

    try {
      User user = userRepository.findByEmail(username)
          .orElseThrow(() -> {
            logger.warn("User not found with email: {}", username);
            return new UsernameNotFoundException("User not found with email: " + username);
          });

      logger.debug("User found for email {}: {}", username, user.getFullName());
      return new CustomUserDetails(user);
    } catch (Exception e) {
      if (!(e instanceof UsernameNotFoundException)) {
        logger.error("Error while loading user by username {}: {}", username, e.getMessage());
      }
      throw e;
    }
  }
}