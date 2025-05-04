package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.PositionDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * The type User service.
 */
@Service
public class UserService {
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  private final NotificationService notificationService;

  /**
   * Instantiates a new User service.
   *
   * @param userRepository      the user repository
   * @param notificationService the notification service
   */
  public UserService(UserRepository userRepository, NotificationService notificationService) {
    this.userRepository = userRepository;
    this.notificationService = notificationService;
  }


  /**
   * Gets current user.
   *
   * @return the current user
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
   * Check if an email address exists.
   *
   * @param email the email
   * @return the userId
   */
  public Long checkIfMailExists(String email) {
    User user = userRepository.getUserByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user with this email"));
    return user.getId();
  }

  /**
   * Gets a user's household.
   *
   * @param userId the user id
   * @return the household
   */
  public HouseholdResponseDto getHousehold(Long userId) {
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

  /**
   * Updates a user's position on the map, and notifies other users in the same household.
   *
   * @param position the position
   */
  public void updatePosition(PositionDto position) {
    User user = userRepository.getUsersById(position.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("No user found"));

    user.setLongitude(position.getLongitude());
    user.setLatitude(position.getLatitude());

    userRepository.save(user);
    notificationService.sendHouseholdPositionUpdate(user.getHousehold().getId(), position);
  }
}
