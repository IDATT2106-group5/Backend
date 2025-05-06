package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.household.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.DeleteHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.EditHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdBasicResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.EditMemberDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.RemoveUnregisteredMemberRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.GetUserInfoRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserHouseholdAssignmentRequestDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  /**
   * The Household service.
   */
  HouseholdService householdService;

  /**
   * Instantiates a new Household controller.
   *
   * @param householdService the household service
   */
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
      LOGGER.info("User added to household successfully: {}", request.getUserId());
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
   * @param request The request containing the full name of the unregistered member and the ID the
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
   * @param request the request
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Removes a user from a household",
      description = "Removes a user from a household with the given ID")
  @PostMapping("/remove-user")
  public ResponseEntity<String> removeUserFromHousehold(
      @RequestBody UserHouseholdAssignmentRequestDto request) {
    try {
      householdService.removeUserFromHousehold(request.getUserId(), request.getHouseholdId());
      LOGGER.info("User removed from household successfully: {}", request.getUserId());
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
   * Allows a user to leave their household. The user must not be the owner of the household.
   *
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Allows a user to leave their household",
      description = "Authenticated user leaves their current household, if not the owner")
  @PostMapping("/leave")
  public ResponseEntity<String> leaveHousehold() {
    try {
      householdService.leaveCurrentUserFromHousehold();
      return ResponseEntity.ok("Du har forlatt husstanden.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Uventet feil under utmelding fra husstand.");
    }
  }

  /**
   * Deletes a household with the given ID. The user must be the owner of the household.
   *
   * @param request the request
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Deletes a household", description = "Deletes the household with the given ID")
  @PostMapping("/delete")
  public ResponseEntity<String> deleteHousehold(@RequestBody DeleteHouseholdRequestDto request) {
    try {
      householdService.deleteHousehold(request.getHouseholdId(), request.getOwnerId());
      LOGGER.info("Household deleted successfully: householdId={}, ownerId={}",
          request.getHouseholdId(), request.getOwnerId());
      return ResponseEntity.ok("Household deleted successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during household deletion: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error(
          "Unexpected error during household deletion: householdId={}, ownerId={}, message={}",
          request.getHouseholdId(), request.getOwnerId(), e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Removes an unregistered member from a household with the given ID. The user must not be
   * registered in the system.
   *
   * @param request the request
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Removes an unregistered member from a household",
      description = "Removes an unregistered user from a household with the given ID")
  @PostMapping("/delete-unregistered-member")
  public ResponseEntity<String> removeUnregisteredMemberFromHousehold(
      @RequestBody RemoveUnregisteredMemberRequestDto request) {
    try {
      householdService.removeUnregisteredMemberFromHousehold(request.getMemberId());
      LOGGER.info("Unregistered member removed from household successfully");
      return ResponseEntity.ok("Unregistered member removed from household successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during unregistered member removal: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during unregistered member removal: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Gets household details.
   *
   * @param request the request
   * @return the household details
   */
  @PostMapping("/details")
  public ResponseEntity<?> getHouseholdDetails(@RequestBody GetUserInfoRequestDto request) {
    try {
      Map<String, Object> details = householdService.getHouseholdDetails(request.getUserId());

      if (details == null || details.get("household") == null) {
        LOGGER.info("User with ID {} has no household", request.getUserId());
        return ResponseEntity.status(404).body(Map.of("error", "Brukeren tilhører ingen husstand"));
      }
      LOGGER.info("Household members retrieved successfully: {}", details);
      return ResponseEntity.ok(details);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during household member retrieval: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error during household member retrieval: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }


  /**
   * Edit unregistered member in household response entity.
   *
   * @param request the request
   * @return the response entity
   */
  @Operation(summary = "Edits a unregistered member in a household",
      description = "Edits a unregistered member in a household with the given ID")
  @PostMapping("/edit-unregistered-member")
  public ResponseEntity<String> editUnregisteredMemberInHousehold(
      @RequestBody EditMemberDto request) {
    try {
      householdService.editUnregisteredMemberInHousehold(request);
      LOGGER.info("Unregistered member edited in household successfully: {}",
          request.getNewFullName());
      return ResponseEntity.ok("Unregistered member edited in household successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during unregistered member edit: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during unregistered member edit: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Changes the owner of a household.
   *
   * @param request the request
   * @return the response entity
   */
  @Operation(summary = "Changes the owner of a household",
      description = "Changes the owner of a household with the given ID")
  @PostMapping("/change-owner")
  public ResponseEntity<?> changeHouseholdOwner(
      @RequestBody UserHouseholdAssignmentRequestDto request) {
    try {
      householdService.changeHouseholdOwner(request);
      LOGGER.info("Household owner changed successfully: {}", request.getUserId());
      return ResponseEntity.ok("Household owner changed successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during household owner change: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error during household owner change: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Edits a household's details.
   *
   * @param request the request
   * @return the response entity
   */
  @Operation(summary = "Edits the details of a household",
      description = "Edits the details of a household with the given ID")
  @PostMapping("/edit")
  public ResponseEntity<String> editHousehold(
      @RequestBody EditHouseholdRequestDto request) {
    try {
      householdService.editHousehold(request);
      LOGGER.info("Household edited successfully: {}", request.getName());
      return ResponseEntity.ok("Household edited successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during household edit: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during household edit: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Search for a household by household id.
   *
   * @param request the request
   * @return the response entity
   */
  @Operation(summary = "Search for a household by household id", description = "Search for a household by household id")
  @PostMapping("/search")
  public ResponseEntity<?> searchHouseholdById(@RequestBody Map<String, Long> request) {
    Long householdId = request.get("householdId");
    LOGGER.info("Received request to search for household with ID: {}", householdId);
    try {
      HouseholdBasicResponseDto response = householdService.searchHouseholdById(householdId);
      LOGGER.info("Household found with ID: {}", response.getId());
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Household search failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error during household search", e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }
}
