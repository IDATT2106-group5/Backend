package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.MembershipRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.GetUserInfoRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.MembershipRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MembershipRequest", description = "Endpoints for managing membership requests")
@RestController
@RequestMapping("/api/membership-requests")
public class MembershipRequestController {
  private final MembershipRequestService membershipRequestService;
  private static final Logger LOGGER = LoggerFactory.getLogger(MembershipRequestController.class);

  public MembershipRequestController(MembershipRequestService membershipRequestService) {
    this.membershipRequestService = membershipRequestService;
  }

  @Operation(summary = "Get active membership requests", description = "Retrieves all active membership requests for a given user")
  @GetMapping("/active")
  public ResponseEntity<Map<String, Object>> getActiveMembershipRequests(
      @RequestBody GetUserInfoRequestDto request) {
    try {
      Map<String, Object> result = Map.of(
          "active requests", membershipRequestService.getActiveRequestsByUser(request.getUserId()));
      LOGGER.info("Retrieved active membership requests for user: {}", request.getUserId());
      return ResponseEntity.ok(result);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error retrieving requests: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error retrieving requests: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  @Operation(summary = "Send a membership invitation", description = "Sends a membership invitation to a user for a given household")
  @PostMapping("/send-invitation")
  public ResponseEntity<String> sendInvitation(@RequestBody MembershipRequestDto request) {
    try {
      membershipRequestService.sendInvitation(request);
      LOGGER.info("Invitation sent successfully: {}", request.getUserEmail());
      return ResponseEntity.ok("Invitation sent successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Invitation failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during invitation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  @Operation(summary = "Send a join request", description = "Sends a join request to a household for a given user")
  @PostMapping("/send-join-request")
  public ResponseEntity<String> sendJoinRequest(@RequestBody MembershipRequestDto request) {
    try {
      membershipRequestService.sendJoinRequest(request);
      LOGGER.info("Join request sent successfully: {}", request.getUserEmail());
      return ResponseEntity.ok("Join request sent successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Join request failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error sending join request: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  @Operation(summary = "Decline a membership request", description = "Declines a membership request with the given ID")
  @PostMapping("/decline")
  public ResponseEntity<String> declineRequest(@RequestParam Long requestId) {
    try {
      membershipRequestService.declineRequest(requestId);
      LOGGER.info("Request declined successfully: {}", requestId);
      return ResponseEntity.ok("Request declined successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Request decline failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error declining request: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  @Operation(summary = "Accept a membership request", description = "Accepts a membership request with the given ID")
  @PostMapping("/accept")
  public ResponseEntity<String> acceptRequest(@RequestParam Long requestId) {
    try {
      membershipRequestService.acceptRequest(requestId);
      LOGGER.info("Request accepted successfully: {}", requestId);
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