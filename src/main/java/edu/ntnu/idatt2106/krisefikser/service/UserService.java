package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.PositionDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
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
    logger.info("UserService instantiated");
  }

  /**
   * Gets current user.
   *
   * @return the current user
   */
  public UserResponseDto getCurrentUser() {
    logger.info("getCurrentUser() called");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    logger.debug("Authenticated email: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.error("No user logged in with email={}", email);
          return new IllegalArgumentException("No user logged in");
        });
    logger.info("User found: id={}, email={}", user.getId(), user.getEmail());

    UserResponseDto userDto = new UserResponseDto(
        user.getId(),
        user.getEmail(),
        user.getFullName(),
        user.getTlf(),
        user.getRole()
    );
    logger.debug("Returning UserResponseDto: {}", userDto);
    return userDto;
  }

  /**
   * Check if an email address exists.
   *
   * @param email the email
   * @return the userId
   */
  public String checkIfMailExists(String email) {
    logger.info("checkIfMailExists() called for email={}", email);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.error("No user found with email={}", email);
          return new IllegalArgumentException("No user with this email");
        });
    logger.info("Email exists for userId={}", user.getId());
    return user.getId();
  }

  /**
   * Gets a user's household.
   *
   * @return the household
   */
  public HouseholdResponseDto getHousehold() {
    logger.info("getHousehold() called for current user");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Household household = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.error("No user logged in with email={}", email);
          return new IllegalArgumentException("No user logged in");
        }).getHousehold();

    logger.info("Found household: id={}, name={}", household.getId(), household.getName());

    HouseholdResponseDto dto = new HouseholdResponseDto(
        household.getId(),
        household.getName(),
        household.getAddress(),
        new UserResponseDto(
            household.getOwner().getId(),
            household.getOwner().getEmail(),
            household.getOwner().getFullName(),
            household.getOwner().getTlf(),
            household.getOwner().getRole()
        )
    );
    logger.debug("Returning HouseholdResponseDto: {}", dto);
    return dto;
  }

  /**
   * Gets all users with ADMIN and SUPERADMIN role.
   *
   * @return List of admin users as DTOs
   */
  public List<UserResponseDto> getAllAdmins() {
    logger.info("getAllAdmins() called");
    List<User> adminUsers = userRepository.findAll().stream()
        .filter(user -> user.getRole() == Role.ADMIN || user.getRole() == Role.SUPERADMIN)
        .toList();
    logger.info("Found {} admin/superadmin users", adminUsers.size());

    List<UserResponseDto> dtos = adminUsers.stream()
        .map(admin -> {
          logger.debug("Mapping admin user id={} to DTO", admin.getId());
          return new UserResponseDto(
              admin.getId(),
              admin.getEmail(),
              admin.getFullName(),
              admin.getTlf(),
              admin.getRole()
          );
        })
        .collect(Collectors.toList());
    logger.info("Returning {} UserResponseDto objects for admins", dtos.size());
    return dtos;
  }

  /**
   * Updates a user's position on the map, and notifies other users in the same household.
   *
   * @param position the position
   */
  public void updatePosition(PositionDto position) {
    logger.info("updatePosition() called for current user, lat={}, lon={}",
        position.getLatitude(), position.getLongitude());

    String email = extractUserFromToken(position.getToken());

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.error("No user found with email={}", email);
          return new IllegalArgumentException("No user found");
        });

    user.setLongitude(position.getLongitude());
    user.setLatitude(position.getLatitude());
    userRepository.save(user);
    logger.info("Saved new position for userId={}", user.getId());

    notificationService.sendHouseholdPositionUpdate(user.getId(), user.getHousehold().getId(),
        position);
    logger.info("Sent household position update notification for householdId={}",
        user.getHousehold().getId());
  }

  /**
   * Extracts the user ID from the token.
   *
   * @param token the JWT token.
   * @return the user ID
   */
  private String extractUserFromToken(String token) {
    logger.info("extractUserFromToken() called with token={}", token);
    try {
      if (token == null || token.isEmpty()) {
        logger.error("Token is null or empty");
        throw new IllegalArgumentException("Invalid token");
      }

      // Extract parts of JWT token
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        logger.error("Invalid JWT token format");
        throw new IllegalArgumentException("Invalid JWT format");
      }

      // Decode payload (middle part of JWT)
      String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

      // Parse JSON payload
      org.json.JSONObject jsonPayload = new org.json.JSONObject(payload);
      String subject = jsonPayload.getString("sub");

      logger.debug("Successfully extracted userId={} from token", subject);
      return subject;
    } catch (Exception e) {
      logger.error("Error extracting user from token: {}", e.getMessage(), e);
      throw new IllegalArgumentException("Failed to extract user from token", e);
    }
  }
}
