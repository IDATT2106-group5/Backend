package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.UnregisteredMemberHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.HouseholdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing household-related operations. This includes creating households,
 * adding/removing users, and managing unregistered members.
 */

@Tag(name = "Household", description = "Endpoints for managing a household")
@RestController
@RequestMapping("/api/household")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class HouseholdController {

  private static final Logger LOGGER = LoggerFactory.getLogger(HouseholdController.class);
  HouseholdService householdService;

  public HouseholdController(HouseholdService householdService) {
    this.householdService = householdService;
  }

  /**
   * Creates a new household with the given name and address. The creator automatically becomes the
   * owner of the household.
   *
   * @param request The request containing the name and address of the household.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Creates a household",
      description = "Creates a household with the given name and address for a given user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Household created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid household data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping("/create")
  public ResponseEntity<String> createHousehold(
      @RequestBody CreateHouseholdRequestDto request) {
    try {
      householdService.createHousehold(request);
      LOGGER.info("Household created successfully: {}", request.getName());
      return ResponseEntity.ok("Household created successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during household creation: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during household creation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Adds a user to a household with the given ID. The user must be registered in the system.
   *
   * @param request The request containing the email of the user and the ID of the household.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Adds a user to a household",
      description = "Adds a user to a household with the given ID")
  @PostMapping("/add-user")
  public ResponseEntity<String> addUserToHousehold(
      @RequestBody UserHouseholdAssignmentRequestDto request) {
    try {
      householdService.addUserToHousehold(request);
      LOGGER.info("User added to household successfully: {}", request.getEmail());
      return ResponseEntity.ok("User added to household successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during user addition: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during user addition: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Adds an unregistered member to a household with the given ID. The user must not be registered
   * in the system.
   *
   * @param request The request containing the full name of the unregistered member and the ID of
   *                the
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Adds an unregistered member to a household",
      description = "Adds an unregistered user to a household with the given ID")
  @PostMapping("/add-unregistered-member")
  public ResponseEntity<String> addUnregisteredMemberToHousehold(
      @RequestBody UnregisteredMemberHouseholdAssignmentRequestDto request) {
    try {
      householdService.addUnregisteredMemberToHousehold(request);
      LOGGER.info("Unregistered member added to household successfully: {}", request.getFullName());
      return ResponseEntity.ok("Unregistered member added to household successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during unregistered member addition: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during unregistered member addition: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Removes a user from a household with the given ID. The user must be registered in the system.
   *
   * @param email The email of the user to be removed from the household.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Removes a user from a household",
      description = "Removes a user from a household with the given ID")
  @PostMapping("/remove-user")
  public ResponseEntity<String> removeUserFromHousehold(
      @RequestBody String email) {
    try {
      householdService.removeUserFromHousehold(email);
      LOGGER.info("User removed from household successfully: {}", email);
      return ResponseEntity.ok("User removed from household successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during user removal: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during user removal: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Removes an unregistered member from a household with the given ID. The user must not be
   * registered in the system.
   *
   * @param request The request containing the full name of the unregistered member and the ID of
   *                the
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Removes an unregistered member from a household",
      description = "Removes an unregistered user from a household with the given ID")
  @DeleteMapping("/delete-unregistered-member")
  public ResponseEntity<String> removeUnregisteredMemberFromHousehold(
      @RequestBody UnregisteredMemberHouseholdAssignmentRequestDto request) {
    try {
      householdService.removeUnregisteredMemberFromHousehold(request);
      LOGGER.info("Unregistered member removed from household successfully: {}",
          request.getFullName());
      return ResponseEntity.ok("Unregistered member removed from household successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during unregistered member removal: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during unregistered member removal: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  @Operation(summary = "Gets the members of a household", description = "Gets the members of a household with the given ID")
  @GetMapping("/members")
  public ResponseEntity<Map<String, Object>> getHouseholdMembers(
      @RequestParam Long householdId) {
    try {
      Map<String, Object> members = householdService.getMembersByHouseholdId(householdId);
      LOGGER.info("Household members retrieved successfully: {}", members);
      return ResponseEntity.ok(members);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during household member retrieval: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error during household member retrieval: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }
}
