package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipInviteDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.RequestOperationDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.service.MembershipRequestService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class MembershipRequestControllerTest {

  @Mock
  private MembershipRequestService membershipRequestService;

  @InjectMocks
  private MembershipRequestController membershipRequestController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private MembershipRequestResponseDto testResponse;
  private UserResponseDto senderDto;
  private UserResponseDto recipientDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(membershipRequestController).build();
    objectMapper = new ObjectMapper();

    senderDto = new UserResponseDto("sender-123", "sender@example.com", "John Sender",
        "+4712345678", Role.USER);
    recipientDto = new UserResponseDto("recipient-456", "recipient@example.com", "Jane Recipient",
        "+4787654321", Role.USER);

    testResponse = new MembershipRequestResponseDto(
        1L,
        "household-123",
        "Test Household",
        senderDto,
        recipientDto,
        RequestType.INVITATION,
        RequestStatus.PENDING,
        Timestamp.valueOf(LocalDateTime.now())
    );
  }

  @Test
  void getActiveInvitations_shouldReturnOkWithInvitations() throws Exception {
    // Arrange
    List<MembershipRequestResponseDto> invitations = List.of(testResponse);
    when(membershipRequestService.getReceivedInvitationsByUser()).thenReturn(invitations);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/received")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].householdId").value("household-123"))
        .andExpect(jsonPath("$[0].householdName").value("Test Household"))
        .andExpect(jsonPath("$[0].requestType").value(RequestType.INVITATION.toString()));

    verify(membershipRequestService).getReceivedInvitationsByUser();
  }

  @Test
  void getActiveInvitations_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
    // Arrange
    when(membershipRequestService.getReceivedInvitationsByUser())
        .thenThrow(new IllegalArgumentException("Invalid request"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/received")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid request"));
  }

  @Test
  void getActiveInvitations_shouldReturnInternalServerError_whenGeneralException()
      throws Exception {
    // Arrange
    when(membershipRequestService.getReceivedInvitationsByUser())
        .thenThrow(new RuntimeException("Server error"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/received")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }

  @Test
  void getActiveJoinRequests_shouldReturnOkWithRequests() throws Exception {
    // Arrange
    MembershipRequestResponseDto joinRequest = new MembershipRequestResponseDto(
        2L,
        "household-123",
        "Test Household",
        senderDto,
        recipientDto,
        RequestType.JOIN_REQUEST,
        RequestStatus.PENDING,
        Timestamp.valueOf(LocalDateTime.now())
    );
    List<MembershipRequestResponseDto> requests = List.of(joinRequest);
    when(membershipRequestService.getReceivedJoinRequestsByHousehold()).thenReturn(requests);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/join-requests/received")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(2L))
        .andExpect(jsonPath("$[0].requestType").value(RequestType.JOIN_REQUEST.toString()));

    verify(membershipRequestService).getReceivedJoinRequestsByHousehold();
  }

  @Test
  void getActiveJoinRequests_shouldReturnBadRequest_whenIllegalArgumentException()
      throws Exception {
    // Arrange
    when(membershipRequestService.getReceivedJoinRequestsByHousehold())
        .thenThrow(new IllegalArgumentException("Invalid request"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/join-requests/received")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid request"));
  }

  @Test
  void getActiveJoinRequests_shouldReturnInternalServerError_whenGeneralException()
      throws Exception {
    // Arrange
    when(membershipRequestService.getReceivedJoinRequestsByHousehold())
        .thenThrow(new RuntimeException("Server error"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/join-requests/received")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }

  @Test
  void getActiveAcceptedJoinRequests_shouldReturnOkWithRequests() throws Exception {
    // Arrange
    MembershipRequestResponseDto acceptedRequest = new MembershipRequestResponseDto(
        3L,
        "household-123",
        "Test Household",
        senderDto,
        recipientDto,
        RequestType.JOIN_REQUEST,
        RequestStatus.ACCEPTED,
        Timestamp.valueOf(LocalDateTime.now())
    );
    List<MembershipRequestResponseDto> requests = List.of(acceptedRequest);
    when(membershipRequestService.getAcceptedReceivedJoinRequestsByHousehold()).thenReturn(
        requests);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/join-requests/received/accepted")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(3L))
        .andExpect(jsonPath("$[0].status").value(RequestStatus.ACCEPTED.toString()));

    verify(membershipRequestService).getAcceptedReceivedJoinRequestsByHousehold();
  }

  @Test
  void getActiveAcceptedJoinRequests_shouldReturnBadRequest_whenIllegalArgumentException()
      throws Exception {
    // Arrange
    when(membershipRequestService.getAcceptedReceivedJoinRequestsByHousehold())
        .thenThrow(new IllegalArgumentException("Invalid request"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/join-requests/received/accepted")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid request"));
  }

  @Test
  void getActiveAcceptedJoinRequests_shouldReturnInternalServerError_whenGeneralException()
      throws Exception {
    // Arrange
    when(membershipRequestService.getAcceptedReceivedJoinRequestsByHousehold())
        .thenThrow(new RuntimeException("Server error"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/join-requests/received/accepted")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }

  @Test
  void getActiveRequests_shouldReturnOkWithRequests() throws Exception {
    // Arrange
    List<MembershipRequestResponseDto> requests = List.of(testResponse);
    when(membershipRequestService.getReceivedInvitationsByUser()).thenReturn(requests);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/sent")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].householdName").value("Test Household"));

    verify(membershipRequestService).getReceivedInvitationsByUser();
  }

  @Test
  void getActiveRequests_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
    // Arrange
    when(membershipRequestService.getReceivedInvitationsByUser())
        .thenThrow(new IllegalArgumentException("Invalid request"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/sent")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid request"));
  }

  @Test
  void getActiveRequests_shouldReturnInternalServerError_whenGeneralException() throws Exception {
    // Arrange
    when(membershipRequestService.getReceivedInvitationsByUser())
        .thenThrow(new RuntimeException("Server error"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/sent")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }

  @Test
  void sendInvitation_shouldReturnOk() throws Exception {
    // Arrange
    MembershipInviteDto inviteDto = new MembershipInviteDto("test@example.com");
    doNothing().when(membershipRequestService).sendInvitation(anyString());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/send-invitation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inviteDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.Message").value("Invitation sent successfully"));

    verify(membershipRequestService).sendInvitation("test@example.com");
  }

  @Test
  void sendInvitation_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
    // Arrange
    MembershipInviteDto inviteDto = new MembershipInviteDto("invalid@example.com");
    doThrow(new IllegalArgumentException("User not found"))
        .when(membershipRequestService).sendInvitation(anyString());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/send-invitation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inviteDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("User not found"));
  }

  @Test
  void sendInvitation_shouldReturnInternalServerError_whenGeneralException() throws Exception {
    // Arrange
    MembershipInviteDto inviteDto = new MembershipInviteDto("error@example.com");
    doThrow(new RuntimeException("Server error"))
        .when(membershipRequestService).sendInvitation(anyString());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/send-invitation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inviteDto)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }

  @Test
  void getInvitationsSentByHousehold_shouldReturnOkWithInvitations() throws Exception {
    // Arrange
    List<MembershipRequestResponseDto> invitations = List.of(testResponse);
    when(membershipRequestService.getInvitationsSentByHousehold()).thenReturn(invitations);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/sent/by-household")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].householdName").value("Test Household"));

    verify(membershipRequestService).getInvitationsSentByHousehold();
  }

  @Test
  void getInvitationsSentByHousehold_shouldReturnBadRequest_whenIllegalArgumentException()
      throws Exception {
    // Arrange
    when(membershipRequestService.getInvitationsSentByHousehold())
        .thenThrow(new IllegalArgumentException("Invalid request"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/sent/by-household")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid request"));
  }

  @Test
  void getInvitationsSentByHousehold_shouldReturnInternalServerError_whenGeneralException()
      throws Exception {
    // Arrange
    when(membershipRequestService.getInvitationsSentByHousehold())
        .thenThrow(new RuntimeException("Server error"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/sent/by-household")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }

  @Test
  void sendJoinRequest_shouldReturnOk() throws Exception {
    // Arrange
    Map<String, String> requestBody = Map.of("householdId", "household-123");
    doNothing().when(membershipRequestService).sendJoinRequest(anyString());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/send-join-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk())
        .andExpect(content().string("Join request sent successfully"));

    verify(membershipRequestService).sendJoinRequest("household-123");
  }

  @Test
  void sendJoinRequest_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
    // Arrange
    Map<String, String> requestBody = Map.of("householdId", "invalid-id");
    doThrow(new IllegalArgumentException("Household not found"))
        .when(membershipRequestService).sendJoinRequest(anyString());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/send-join-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Household not found"));
  }

  @Test
  void sendJoinRequest_shouldReturnInternalServerError_whenGeneralException() throws Exception {
    // Arrange
    Map<String, String> requestBody = Map.of("householdId", "error-id");
    doThrow(new RuntimeException("Server error"))
        .when(membershipRequestService).sendJoinRequest(anyString());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/send-join-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Internal server error"));
  }

  @Test
  void declineRequest_shouldReturnOk() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 1L);

    doNothing().when(membershipRequestService).declineRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/decline")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Request declined successfully"));

    verify(membershipRequestService).declineRequest(1L);
  }

  // Helper method to set private fields using reflection
  private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
    java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(object, value);
  }

  @Test
  void declineRequest_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 999L);

    doThrow(new IllegalArgumentException("Request not found"))
        .when(membershipRequestService).declineRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/decline")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Request not found"));
  }

  @Test
  void declineRequest_shouldReturnInternalServerError_whenGeneralException() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 1L);

    doThrow(new RuntimeException("Server error"))
        .when(membershipRequestService).declineRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/decline")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Internal server error"));
  }

  @Test
  void acceptJoinRequest_shouldReturnOk() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 1L);

    doNothing().when(membershipRequestService).acceptJoinRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/accept-join-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Request accepted successfully"));

    verify(membershipRequestService).acceptJoinRequest(1L);
  }

  @Test
  void acceptJoinRequest_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 999L);

    doThrow(new IllegalArgumentException("Request not found"))
        .when(membershipRequestService).acceptJoinRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/accept-join-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Request not found"));
  }

  @Test
  void acceptJoinRequest_shouldReturnInternalServerError_whenGeneralException() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 1L);

    doThrow(new RuntimeException("Server error"))
        .when(membershipRequestService).acceptJoinRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/accept-join-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Internal server error"));
  }

  @Test
  void acceptInvitationRequest_shouldReturnOk() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 1L);

    doNothing().when(membershipRequestService).acceptInvitationRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/accept-invitation-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Request accepted successfully"));

    verify(membershipRequestService).acceptInvitationRequest(1L);
  }

  @Test
  void acceptInvitationRequest_shouldReturnBadRequest_whenIllegalArgumentException()
      throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 999L);

    doThrow(new IllegalArgumentException("Request not found"))
        .when(membershipRequestService).acceptInvitationRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/accept-invitation-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Request not found"));
  }

  @Test
  void acceptInvitationRequest_shouldReturnInternalServerError_whenGeneralException()
      throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 1L);

    doThrow(new RuntimeException("Server error"))
        .when(membershipRequestService).acceptInvitationRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/accept-invitation-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Internal server error"));
  }

  @Test
  void cancelRequest_shouldReturnOk() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 1L);

    doNothing().when(membershipRequestService).cancelRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Request accepted successfully"));

    verify(membershipRequestService).cancelRequest(1L);
  }

  @Test
  void cancelRequest_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 999L);

    doThrow(new IllegalArgumentException("Request not found"))
        .when(membershipRequestService).cancelRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Request not found"));
  }

  @Test
  void cancelRequest_shouldReturnInternalServerError_whenGeneralException() throws Exception {
    // Arrange
    RequestOperationDto request = new RequestOperationDto();
    setPrivateField(request, "requestId", 1L);

    doThrow(new RuntimeException("Server error"))
        .when(membershipRequestService).cancelRequest(anyLong());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Internal server error"));
  }
}