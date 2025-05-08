package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipInviteDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.RequestOperationDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.GetUserInfoRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.MembershipRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
 * Controller for handling membership request related operations.
 */
@Tag(name = "MembershipRequest", description = "Endpoints for managing membership requests")
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/membership-requests")
public class MembershipRequestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MembershipRequestController.class);
  private final MembershipRequestService membershipRequestService;

  /**
   * Constructor for MembershipRequestController.
   *
   * @param membershipRequestService the service for handling membership requests
   */
  public MembershipRequestController(MembershipRequestService membershipRequestService) {
    this.membershipRequestService = membershipRequestService;
  }

  /**
   * Gets all active invitations for a given user.
   *
   * @param request the request containing the user ID
   * @return a response entity with the list of active invitations
   */
  @Operation(summary = "Get active membership requests",
      description = "Retrieves all active membership requests for a given user")
  @PostMapping("/invitations/received")
  public ResponseEntity<?> getActiveInvitations(
      @RequestBody GetUserInfoRequestDto request) {
    try {
      List<MembershipRequestResponseDto> member =
          membershipRequestService.getReceivedInvitationsByUser(request.getUserId());
      LOGGER.info("Retrieved received invitations for user: {}", request.getUserId());
      return ResponseEntity.ok(member);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error retrieving received invitations: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error retrieving requests: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Gets all active join requests sent to a household.
   *
   * @param request the request containing the household ID
   * @return a response entity with the list of active join requests
   */
  @Operation(summary = "Gets all active join requests sent to a household",
      description = "Retrieves all active join requests sent to a household")
  @PostMapping("/join-requests/received")
  public ResponseEntity<?> getActiveJoinRequests(
      @RequestBody Map<String, String> request) {
    try {
      List<MembershipRequestResponseDto> requests =
          membershipRequestService.getReceivedJoinRequestsByHousehold(request.get("householdId"));
      return ResponseEntity.ok(requests);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error retrieving join requests: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error retrieving join requests: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Gets all accepted join requests sent to a household.
   *
   * @param request the request containing the household ID
   * @return a response entity with the list of accepted join requests
   */
  @Operation(summary = "Gets all accepted join requests sent to a household",
      description = "Retrieves all accepted join requests sent to a household")
  @PostMapping("/join-requests/received/accepted")
  public ResponseEntity<?> getActiveAcceptedJoinRequests(
      @RequestBody Map<String, String> request) {
    try {
      List<MembershipRequestResponseDto> requests =
          membershipRequestService.getAcceptedReceivedJoinRequestsByHousehold(
              request.get("householdId"));
      return ResponseEntity.ok(requests);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error retrieving join requests: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error retrieving join requests: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Gets all active invitations for a given user.
   *
   * @param request the request containing the user ID
   * @return a response entity with the list of active invitations
   */
  @Operation(summary = "Get active invitations",
      description = "Retrieves all active invitations sent to a given user")
  @PostMapping("/invitations/sent")
  public ResponseEntity<?> getActiveRequests(
      @RequestBody GetUserInfoRequestDto request) {
    try {
      List<MembershipRequestResponseDto> member =
          membershipRequestService.getReceivedInvitationsByUser(request.getUserId());
      LOGGER.info("Retrieved sent membership invitations for user: {}", request.getUserId());
      return ResponseEntity.ok(member);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error retrieving sent invitations: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error retrieving requests: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Sends a membership invitation to a user for a given household.
   *
   * @param request the request containing the email and household ID
   * @return a response entity with a message
   */
  @Operation(summary = "Send a membership invitation",
      description = "Sends a membership invitation to a user for a given household")
  @PostMapping("/send-invitation")
  public ResponseEntity<Map<String, String>> sendInvitation(
      @RequestBody MembershipInviteDto request) {
    try {
      membershipRequestService.sendInvitation(request.getEmail(), request.getHouseholdId());
      LOGGER.info("Invitation sent successfully to {}", request.getEmail());
      return ResponseEntity.ok(Map.of("Message", "Invitation sent successfully"));
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Invitation failed: {}", e.getMessage());
      return ResponseEntity.badRequest()
          .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error during invitation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Gets all invitations sent by a household.
   *
   * @param request the request containing the household ID
   * @return a response entity with the list of invitations
   */
  @Operation(summary = "Get all invitations sent by a household",
      description = "Returns all membership invitations sent from a household to users")
  @PostMapping("/invitations/sent/by-household")
  public ResponseEntity<?> getInvitationsSentByHousehold(@RequestBody MembershipInviteDto request) {
    try {
      String householdId = request.getHouseholdId();
      List<MembershipRequestResponseDto> invitations =
          membershipRequestService.getInvitationsSentByHousehold(householdId);
      LOGGER.info("Retrieved sent invitations for household: {}", householdId);
      return ResponseEntity.ok(invitations);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error retrieving sent invitations: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error retrieving sent invitations: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Sends a join request to a household for a given user.
   *
   * @param request the request containing the user ID and household ID
   * @return a response entity with a message
   */
  @Operation(summary = "Send a join request",
      description = "Sends a join request to a household for a given user")
  @PostMapping("/send-join-request")
  public ResponseEntity<String> sendJoinRequest(@RequestBody MembershipRequestDto request) {
    try {
      membershipRequestService.sendJoinRequest(request);
      LOGGER.info("Join request sent successfully");
      return ResponseEntity.ok("Join request sent successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Join request failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error sending join request: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Declines a membership request.
   *
   * @param request the request containing the request ID
   * @return a response entity with a message
   */
  @Operation(summary = "Decline a membership request",
      description = "Declines a membership request with the given ID")
  @PostMapping("/decline")
  public ResponseEntity<String> declineRequest(@RequestBody RequestOperationDto request) {
    try {
      membershipRequestService.declineRequest(request.getRequestId());
      LOGGER.info("Request declined successfully: {}", request.getRequestId());
      return ResponseEntity.ok("Request declined successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Request decline failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error declining request: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Accepts a membership join request.
   *
   * @param request the request containing the request ID
   * @return a response entity with a message
   */
  @Operation(summary = "Accept a membership join request",
      description = "Accepts a membership join request with the given ID")
  @PostMapping("/accept-join-request")
  public ResponseEntity<String> acceptJoinRequest(@RequestBody RequestOperationDto request) {
    try {
      membershipRequestService.acceptJoinRequest(request.getRequestId());
      LOGGER.info("Request accepted successfully: {}", request.getRequestId());
      return ResponseEntity.ok("Request accepted successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Request acceptance failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error accepting request: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Accepts a membership invitation request.
   *
   * @param request the request containing the request ID
   * @return a response entity with a message
   */
  @Operation(summary = "Accept a membership invitation request",
      description = "Accepts a membership request with the given ID")
  @PostMapping("/accept-invitation-request")
  public ResponseEntity<String> acceptInvitationRequest(@RequestBody RequestOperationDto request) {
    try {
      membershipRequestService.acceptInvitationRequest(request.getRequestId());
      LOGGER.info("Request accepted successfully: {}", request.getRequestId());
      return ResponseEntity.ok("Request accepted successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Request acceptance failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error accepting request: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Cancels a membership request.
   *
   * @param request the request containing the request ID
   * @return a response entity with a message
   */
  @Operation(summary = "Cancels a membership request",
      description = "Cancels a membership request with a given ID")
  @PostMapping("/cancel")
  public ResponseEntity<String> cancelRequest(@RequestBody RequestOperationDto request) {
    try {
      membershipRequestService.cancelRequest(request.getRequestId());
      LOGGER.info("Request accepted successfully: {}", request.getRequestId());
      return ResponseEntity.ok("Request accepted successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Request acceptance failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error accepting request: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }
}