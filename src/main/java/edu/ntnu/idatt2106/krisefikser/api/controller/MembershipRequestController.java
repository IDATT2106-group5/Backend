package edu.ntnu.idatt2106.krisefikser.api.controller;

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
 * Controller for handling membership requests. This includes sending invitations, joining requests,
 * and accepting or declining requests.
 */

@Tag(name = "MembershipRequest", description = "Endpoints for managing membership requests")
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/membership-requests")
public class MembershipRequestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MembershipRequestController.class);
  private final MembershipRequestService membershipRequestService;

  public MembershipRequestController(MembershipRequestService membershipRequestService) {
    this.membershipRequestService = membershipRequestService;
  }

  /**
   * Gets all active membership requests for a given user. This includes both sent and received
   * requests.
   *
   * @param request The request containing the user ID.
   * @return A response entity containing the list of active membership requests.
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
   * Gets all active join requests sent to a household. This includes both sent and received
   * requests.
   *
   * @param request The request containing the household ID.
   * @return A response entity containing the list of active join requests.
   */
  @Operation(summary = "Gets all active join requests sent to a household",
      description = "Retrieves all active join requests sent to a household")
  @PostMapping("/join-requests/received")
  public ResponseEntity<?> getActiveJoinRequests(
      @RequestBody Map<String, Long> request) {
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
   * Gets all accepted join requests sent to a household. This includes both sent and received
   * requests.
   *
   * @param request The request containing the household ID.
   * @return A response entity containing the list of accepted join requests.
   */
  @Operation(summary = "Gets all accepted join requests sent to a household",
      description = "Retrieves all accepted join requests sent to a household")
  @PostMapping("/join-requests/received/accepted")
  public ResponseEntity<?> getActiveAcceptedJoinRequests(
      @RequestBody Map<String, Long> request) {
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
   * Gets all active membership requests sent to a user. This includes both sent and received
   * requests.
   *
   * @param request The request containing the user ID.
   * @return A response entity containing the list of active membership requests.
   */
  @Operation(summary = "Get active membership requests",
      description = "Retrieves all active membership requests for a given user")
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
   * Sends a membership invitation to a user for a given household. This includes both sent and
   * received requests.
   *
   * @param request The request containing the user ID and household ID.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Send a membership invitation",
      description = "Sends a membership invitation to a user for a given household")
  @PostMapping("/send-invitation")
  public ResponseEntity<String> sendInvitation(@RequestBody MembershipRequestDto request) {
    try {
      membershipRequestService.sendInvitation(request);
      LOGGER.info("Invitation sent successfully");
      return ResponseEntity.ok("Invitation sent successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Invitation failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during invitation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Sends a join request to a household for a given user. This includes both sent and received
   * requests.
   *
   * @param request The request containing the user ID and household ID.
   * @return A response entity indicating the result of the operation.
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
   * Declines a membership request with the given ID.
   *
   * @param request The request containing the request ID.
   * @return A response entity indicating the result of the operation.
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
   * Accepts a membership request with the given ID.
   *
   * @param request The request containing the request ID.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Accept a membership request",
      description = "Accepts a membership request with the given ID")
  @PostMapping("/accept")
  public ResponseEntity<String> acceptRequest(@RequestBody RequestOperationDto request) {
    try {
      membershipRequestService.acceptRequest(request.getRequestId());
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
   * Cancels a membership request with the given ID.
   *
   * @param request The request containing the request ID.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Accept a membership request",
      description = "Accepts a membership request with the given ID")
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