package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.MembershipRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.MembershipRequestService;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class MembershipRequestControllerTest {

  private MembershipRequestService membershipRequestService;
  private MembershipRequestController controller;

  @BeforeEach
  void setUp() {
    membershipRequestService = mock(MembershipRequestService.class);
    controller = new MembershipRequestController(membershipRequestService);
  }

  @Test
  void getActiveMembershipRequestsWithValidUserReturnsOk() {
    String userEmail = "user@example.com";
    when(membershipRequestService.getActiveRequestsByUser(userEmail))
        .thenReturn(Collections.emptyList());
    ResponseEntity<Map<String, Object>> response =
        controller.getActiveMembershipRequests(userEmail);

    Map<String, Object> expected = Map.of("active requests", Collections.emptyList());
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(expected, response.getBody());
  }

  @Test
  void getActiveMembershipRequestsWithInvalidUserReturnsBadRequest() {
    String userEmail = "invalid@example.com";
    when(membershipRequestService.getActiveRequestsByUser(userEmail))
        .thenThrow(new IllegalArgumentException("Invalid user email"));
    ResponseEntity<Map<String, Object>> response =
        controller.getActiveMembershipRequests(userEmail);

    assertEquals(400, response.getStatusCodeValue());
    assertTrue(response.getBody().containsKey("error"));
    assertEquals("Invalid user email", response.getBody().get("error"));
  }

  @Test
  void getActiveMembershipRequestsWithUnexpectedErrorReturnsInternalServerError() {
    String userEmail = "error@example.com";
    when(membershipRequestService.getActiveRequestsByUser(userEmail))
        .thenThrow(new RuntimeException("Unexpected error"));
    ResponseEntity<Map<String, Object>> response =
        controller.getActiveMembershipRequests(userEmail);

    assertEquals(500, response.getStatusCodeValue());
    assertTrue(response.getBody().containsKey("error"));
    assertEquals("Internal server error", response.getBody().get("error"));
  }

  @Test
  void sendInvitationWithValidRequestReturnsOk() {
    MembershipRequestDto request = new MembershipRequestDto();
    request.setUserEmail("user@example.com");
    ResponseEntity<String> response = controller.sendInvitation(request);

    verify(membershipRequestService, times(1)).sendInvitation(request);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Invitation sent successfully", response.getBody());
  }

  @Test
  void sendInvitationWithInvalidRequestReturnsBadRequest() {
    MembershipRequestDto request = new MembershipRequestDto();
    request.setUserEmail("user@example.com");
    doThrow(new IllegalArgumentException("Invalid invitation"))
        .when(membershipRequestService).sendInvitation(request);
    ResponseEntity<String> response = controller.sendInvitation(request);

    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Invalid invitation", response.getBody());
  }

  @Test
  void sendInvitationWithUnexpectedErrorReturnsInternalServerError() {
    MembershipRequestDto request = new MembershipRequestDto();
    request.setUserEmail("user@example.com");
    doThrow(new RuntimeException("Unexpected error"))
        .when(membershipRequestService).sendInvitation(request);
    ResponseEntity<String> response = controller.sendInvitation(request);

    assertEquals(500, response.getStatusCodeValue());
    assertEquals("Internal server error", response.getBody());
  }

  @Test
  void sendJoinRequestWithValidRequestReturnsOk() {
    MembershipRequestDto request = new MembershipRequestDto();
    request.setUserEmail("user@example.com");
    ResponseEntity<String> response = controller.sendJoinRequest(request);

    verify(membershipRequestService, times(1)).sendJoinRequest(request);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Join request sent successfully", response.getBody());
  }

  @Test
  void sendJoinRequestWithInvalidRequestReturnsBadRequest() {
    MembershipRequestDto request = new MembershipRequestDto();
    request.setUserEmail("user@example.com");
    doThrow(new IllegalArgumentException("Invalid join request"))
        .when(membershipRequestService).sendJoinRequest(request);
    ResponseEntity<String> response = controller.sendJoinRequest(request);

    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Invalid join request", response.getBody());
  }

  @Test
  void sendJoinRequestWithUnexpectedErrorReturnsInternalServerError() {
    MembershipRequestDto request = new MembershipRequestDto();
    request.setUserEmail("user@example.com");
    doThrow(new RuntimeException("Unexpected error"))
        .when(membershipRequestService).sendJoinRequest(request);
    ResponseEntity<String> response = controller.sendJoinRequest(request);

    assertEquals(500, response.getStatusCodeValue());
    assertEquals("Internal server error", response.getBody());
  }

  @Test
  void declineRequestWithValidRequestIdReturnsOk() {
    Long requestId = 1L;
    ResponseEntity<String> response = controller.declineRequest(requestId);

    verify(membershipRequestService, times(1)).declineRequest(requestId);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Request declined successfully", response.getBody());
  }

  @Test
  void declineRequestWithInvalidRequestIdReturnsBadRequest() {
    Long requestId = 2L;
    doThrow(new IllegalArgumentException("Invalid request ID"))
        .when(membershipRequestService).declineRequest(requestId);
    ResponseEntity<String> response = controller.declineRequest(requestId);

    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Invalid request ID", response.getBody());
  }

  @Test
  void declineRequestWithUnexpectedErrorReturnsInternalServerError() {
    Long requestId = 3L;
    doThrow(new RuntimeException("Unexpected error"))
        .when(membershipRequestService).declineRequest(requestId);
    ResponseEntity<String> response = controller.declineRequest(requestId);

    assertEquals(500, response.getStatusCodeValue());
    assertEquals("Internal server error", response.getBody());
  }

  @Test
  void acceptRequestWithValidRequestIdReturnsOk() {
    Long requestId = 1L;
    ResponseEntity<String> response = controller.acceptRequest(requestId);

    verify(membershipRequestService, times(1)).acceptRequest(requestId);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Request accepted successfully", response.getBody());
  }

  @Test
  void acceptRequestWithInvalidRequestIdReturnsBadRequest() {
    Long requestId = 2L;
    doThrow(new IllegalArgumentException("Invalid request ID"))
        .when(membershipRequestService).acceptRequest(requestId);
    ResponseEntity<String> response = controller.acceptRequest(requestId);

    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Invalid request ID", response.getBody());
  }

  @Test
  void acceptRequestWithUnexpectedErrorReturnsInternalServerError() {
    Long requestId = 3L;
    doThrow(new RuntimeException("Unexpected error"))
        .when(membershipRequestService).acceptRequest(requestId);
    ResponseEntity<String> response = controller.acceptRequest(requestId);

    assertEquals(500, response.getStatusCodeValue());
    assertEquals("Internal server error", response.getBody());
  }
}