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

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(membershipRequestController).build();
    objectMapper = new ObjectMapper();

    // Create test data
    UserResponseDto sender = new UserResponseDto("user1", "sender@example.com", "Sender Name",
        "12345678", Role.USER);
    UserResponseDto recipient = new UserResponseDto("user2", "recipient@example.com",
        "Recipient Name", "87654321", Role.USER);

    testResponse = new MembershipRequestResponseDto(
        1L,
        "household1",
        "Test Household",
        sender,
        recipient,
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
    mockMvc.perform(post("/api/membership-requests/invitations/received"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].householdId").value("household1"))
        .andExpect(jsonPath("$[0].requestType").value("INVITATION"));

    verify(membershipRequestService).getReceivedInvitationsByUser();
  }

  @Test
  void getActiveInvitations_shouldReturnBadRequest_whenIllegalArgumentException() throws Exception {
    // Arrange
    when(membershipRequestService.getReceivedInvitationsByUser())
        .thenThrow(new IllegalArgumentException("User not found"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/received"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("User not found"));
  }

  @Test
  void getActiveInvitations_shouldReturnInternalServerError_whenGeneralException()
      throws Exception {
    // Arrange
    when(membershipRequestService.getReceivedInvitationsByUser())
        .thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/received"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }

  @Test
  void getActiveJoinRequests_shouldReturnOkWithRequests() throws Exception {
    // Arrange
    List<MembershipRequestResponseDto> requests = List.of(testResponse);
    when(membershipRequestService.getReceivedJoinRequestsByHousehold()).thenReturn(requests);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/join-requests/received"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].householdName").value("Test Household"));

    verify(membershipRequestService).getReceivedJoinRequestsByHousehold();
  }

  @Test
  void getActiveAcceptedJoinRequests_shouldReturnOkWithRequests() throws Exception {
    // Arrange
    List<MembershipRequestResponseDto> requests = List.of(testResponse);
    when(membershipRequestService.getAcceptedReceivedJoinRequestsByHousehold()).thenReturn(
        requests);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/join-requests/received/accepted"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].householdId").value("household1"));

    verify(membershipRequestService).getAcceptedReceivedJoinRequestsByHousehold();
  }

  @Test
  void getActiveRequests_shouldReturnOkWithRequests() throws Exception {
    // Arrange
    List<MembershipRequestResponseDto> requests = List.of(testResponse);
    when(membershipRequestService.getReceivedInvitationsByUser()).thenReturn(requests);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/sent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));

    verify(membershipRequestService).getReceivedInvitationsByUser();
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
    MembershipInviteDto inviteDto = new MembershipInviteDto("test@example.com");
    doThrow(new IllegalArgumentException("User not found")).when(membershipRequestService)
        .sendInvitation(anyString());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/send-invitation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inviteDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("User not found"));
  }

  @Test
  void getInvitationsSentByHousehold_shouldReturnOkWithInvitations() throws Exception {
    // Arrange
    List<MembershipRequestResponseDto> invitations = List.of(testResponse);
    when(membershipRequestService.getInvitationsSentByHousehold()).thenReturn(invitations);

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/invitations/sent/by-household"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));

    verify(membershipRequestService).getInvitationsSentByHousehold();
  }

  @Test
  void sendJoinRequest_shouldReturnOk() throws Exception {
    // Arrange
    Map<String, String> request = Map.of("householdId", "household1");
    doNothing().when(membershipRequestService).sendJoinRequest(anyString());

    // Act & Assert
    mockMvc.perform(post("/api/membership-requests/send-join-request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Join request sent successfully"));

    verify(membershipRequestService).sendJoinRequest("household1");
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
}