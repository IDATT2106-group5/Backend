package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing user-related operations.
 * Provides endpoints for retrieving user information.
 */
@Tag(name = "User", description = "Endpoints for managing a user")
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {
  private final UserService userService;
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  /**
   * Constructs a new instance of the UserController.
   *
   * @param userService the service used to manage user-related operations
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Retrieves the details of the currently authenticated user.
   *
   * @return a ResponseEntity containing the user details as a UserResponseDto
   * or an appropriate error response
   */
  @GetMapping("/me")
  public ResponseEntity<?> getUser() {
    try {
      UserResponseDto userDto = userService.getCurrentUser();
      LOGGER.info("Fetched info for current user {}", userDto.getFullName());
      return ResponseEntity.ok(userDto);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Error fetching current user: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Error fetching current user", e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  @GetMapping("/me/household/{userId}")
  public ResponseEntity<?> getHousehold(@PathVariable Long userId) {
    try {
      HouseholdResponseDto household = userService.getHousehold(userId);
      LOGGER.info("Fetched household: {}", household.getName());
      LOGGER.info("Fetched info for current user");
      return ResponseEntity.ok(household);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Error fetching current user: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Error fetching current user", e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }
}