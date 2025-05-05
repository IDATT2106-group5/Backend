package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user-related operations.
 */

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  /**
   * Get the currently logged-in user. This method retrieves the user's email from the
   * SecurityContextHolder and uses it to fetch the user details from the database.
   *
   * @return UserResponseDto containing user details.
   */
  public UserResponseDto getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.getUserByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user logged in"));

    UserResponseDto userDto = new UserResponseDto(
        user.getId(),
        user.getEmail(),
        user.getFullName(),
        user.getTlf(),
        user.getRole()
    );

    return userDto;
  }

  /**
   * Check if a user with the given email exists in the database. If the user exists, return the
   * user's ID.
   *
   * @param email The email address to check.
   * @return The ID of the user if they exist.
   */
  public Long checkIfMailExists(String email) {
    User user = userRepository.getUserByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user with this email"));
    return user.getId();
  }

  /**
   * Get the household of a user. This method retrieves the user's household details based on the
   * user ID provided. It fetches the household from the database and constructs a
   * HouseholdResponseDto object containing the household details.
   *
   * @param userId The ID of the user whose household is to be retrieved.
   * @return HouseholdResponseDto containing household details.
   */
  public HouseholdResponseDto getHousehold(Long userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.getUsersById(userId)
        .orElseThrow(() -> new IllegalArgumentException("No user found"));

    Household household = user.getHousehold();

    return new HouseholdResponseDto(household.getId(),
        household.getName(),
        household.getAddress(),
        new UserResponseDto(household.getOwner().getId(),
            household.getOwner().getEmail(),
            household.getOwner().getFullName(),
            household.getOwner().getTlf(),
            household.getOwner().getRole()));
  }
}
