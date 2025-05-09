package edu.ntnu.idatt2106.krisefikser.api.controller.user;

import edu.ntnu.idatt2106.krisefikser.api.dto.storage.StorageItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.service.storage.StorageService;
import edu.ntnu.idatt2106.krisefikser.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing user-related operations. Provides endpoints for retrieving user
 * information.
 */
@Tag(name = "User", description = "Endpoints for managing a user")
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;
  private final StorageService storageService;

  /**
   * Constructs a new instance of the UserController.
   *
   * @param userService the service used to manage user-related operations
   */
  public UserController(UserService userService, StorageService storageService) {
    this.userService = userService;
    this.storageService = storageService;
  }

  /**
   * Retrieves the details of the currently authenticated user.
   *
   * @return a ResponseEntity containing the user details.
   */
  @Operation(summary = "Gets details about the current user",
      description = "Gets the details about the currently authenticated user")
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

  /**
   * Retrieves the household information for a given user ID.
   *
   * @return a ResponseEntity containing the household information
   */
  @Operation(summary = "Gets the household information for a given user",
      description = "Gets the household information about a user with a given id")
  @GetMapping("/me/household")
  public ResponseEntity<?> getHousehold() {
    try {
      HouseholdResponseDto household = userService.getHousehold();
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

  /**
   * Checks if a given email address exists in the system.
   *
   * @param request a map containing the email address to check
   * @return a ResponseEntity indicating whether the email exists or not
   */
  @Operation(summary = "Check if an email exists in the system",
      description = "Checks if a given email address exists in the system. "
          + "If it does, returns the user ID associated with that email.")
  @PostMapping("/check-mail")
  public ResponseEntity<?> verifyIfMailExists(@RequestBody Map<String, String> request) {
    try {
      String email = request.get("email");
      userService.checkIfMailExists(email);
      LOGGER.info("Email {} exists", email);
      return ResponseEntity.ok(Map.of("userId", userService.checkIfMailExists(email)));
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error checking email: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error checking email: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Retrieves the storage items for the current user's household.
   *
   * @return a ResponseEntity containing the list of storage items.
   */
  @Operation(summary = "Gets the current user's household's storage items",
      description = "Gets the storage items for the current user's household")
  @GetMapping("/me/storage")
  public ResponseEntity<?> getCurrentUserStorageItems() {
    try {
      UserResponseDto userDto = userService.getCurrentUser();
      HouseholdResponseDto household = userService.getHousehold();

      List<StorageItemResponseDto> storageItems = storageService.getStorageItemsByHousehold();
      LOGGER.info("Fetched {} storage items for user {}", storageItems.size(),
          userDto.getFullName());

      return ResponseEntity.ok(storageItems);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Error fetching storage items: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Error fetching storage items", e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }
}