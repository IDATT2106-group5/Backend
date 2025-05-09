package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.MembershipRequest;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MembershipRequestRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MembershipRequestServiceTest {

  @Mock
  private MembershipRequestRepository membershipRequestRepository;

  @Mock
  private HouseholdRepository householdRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private NotificationService notificationService;

  @Mock
  private HouseholdService householdService;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private MembershipRequestService membershipRequestService;

  @Captor
  private ArgumentCaptor<MembershipRequest> membershipRequestCaptor;

  @Captor
  private ArgumentCaptor<NotificationDto> notificationDtoCaptor;

  @Captor
  private ArgumentCaptor<UserHouseholdAssignmentRequestDto> assignmentCaptor;

  private User testUser;
  private User householdOwner;
  private Household testHousehold;
  private MembershipRequest testInvitation;
  private MembershipRequest testJoinRequest;

  @BeforeEach
  void setUp() {
    // Setup Security Context
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Setup test users
    testUser = new User();
    testUser.setId("user-123");
    testUser.setEmail("user@example.com");
    testUser.setFullName("Test User");
    testUser.setTlf("12345678");
    testUser.setRole(Role.USER);

    householdOwner = new User();
    householdOwner.setId("owner-123");
    householdOwner.setEmail("owner@example.com");
    householdOwner.setFullName("Household Owner");
    householdOwner.setTlf("87654321");
    householdOwner.setRole(Role.USER);

    // Setup test household
    testHousehold = new Household();
    testHousehold.setId("household-123");
    testHousehold.setName("Test Household");
    testHousehold.setAddress("123 Test Street");
    testHousehold.setNumberOfMembers(2);
    testHousehold.setOwner(householdOwner);

    householdOwner.setHousehold(testHousehold);

    // Setup test invitation request
    testInvitation = new MembershipRequest();
    testInvitation.setId(1L);
    testInvitation.setHousehold(testHousehold);
    testInvitation.setSender(householdOwner);
    testInvitation.setReceiver(testUser);
    testInvitation.setType(RequestType.INVITATION);
    testInvitation.setStatus(RequestStatus.PENDING);
    testInvitation.setCreatedAt(Timestamp.from(Instant.now()));

    // Setup test join request
    testJoinRequest = new MembershipRequest();
    testJoinRequest.setId(2L);
    testJoinRequest.setHousehold(testHousehold);
    testJoinRequest.setSender(testUser);
    testJoinRequest.setReceiver(householdOwner);
    testJoinRequest.setType(RequestType.JOIN_REQUEST);
    testJoinRequest.setStatus(RequestStatus.PENDING);
    testJoinRequest.setCreatedAt(Timestamp.from(Instant.now()));
  }

  @Test
  void sendInvitation_shouldCreateAndSaveInvitation() {
    // Arrange
    when(authentication.getName()).thenReturn(householdOwner.getEmail());
    when(userRepository.findByEmail(householdOwner.getEmail())).thenReturn(
        Optional.of(householdOwner));
    when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

    // Act
    membershipRequestService.sendInvitation(testUser.getEmail());

    // Assert
    verify(membershipRequestRepository).save(membershipRequestCaptor.capture());
    MembershipRequest capturedRequest = membershipRequestCaptor.getValue();

    assertEquals(testHousehold, capturedRequest.getHousehold());
    assertEquals(householdOwner, capturedRequest.getSender());
    assertEquals(testUser, capturedRequest.getReceiver());
    assertEquals(RequestType.INVITATION, capturedRequest.getType());
    assertEquals(RequestStatus.PENDING, capturedRequest.getStatus());
    assertNotNull(capturedRequest.getCreatedAt());

    verify(notificationService).saveNotification(notificationDtoCaptor.capture());
    NotificationDto capturedNotification = notificationDtoCaptor.getValue();

    assertEquals(NotificationType.MEMBERSHIP_REQUEST, capturedNotification.getType());
    assertEquals(testUser.getId(), capturedNotification.getRecipientId());
    assertFalse(capturedNotification.isRead());
    assertTrue(capturedNotification.getMessage().contains(testHousehold.getName()));

    verify(notificationService).sendPrivateNotification(eq(testUser.getId()),
        any(NotificationDto.class));
  }

  @Test
  void sendInvitation_shouldThrowException_whenUserNotFound() {
    // Arrange
    when(authentication.getName()).thenReturn(householdOwner.getEmail());
    when(userRepository.findByEmail(householdOwner.getEmail()))
        .thenReturn(Optional.of(householdOwner));
    when(userRepository.findByEmail("nonexistent@example.com"))
        .thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(
        IllegalArgumentException.class,
        () -> membershipRequestService.sendInvitation("nonexistent@example.com")
    );

    assertTrue(exception.getMessage()
        .contains("User with email not found"));
    verify(membershipRequestRepository, never())
        .save(any(MembershipRequest.class));
  }

  @Test
  void sendJoinRequest_shouldCreateAndSaveJoinRequest() {
    // Arrange
    when(authentication.getName()).thenReturn(testUser.getEmail());
    when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
    when(householdRepository.findById(testHousehold.getId())).thenReturn(
        Optional.of(testHousehold));

    // Act
    membershipRequestService.sendJoinRequest(testHousehold.getId());

    // Assert
    verify(membershipRequestRepository).save(membershipRequestCaptor.capture());
    MembershipRequest capturedRequest = membershipRequestCaptor.getValue();

    assertEquals(testHousehold, capturedRequest.getHousehold());
    assertEquals(testUser, capturedRequest.getSender());
    assertEquals(householdOwner, capturedRequest.getReceiver());
    assertEquals(RequestType.JOIN_REQUEST, capturedRequest.getType());
    assertEquals(RequestStatus.PENDING, capturedRequest.getStatus());
    assertNotNull(capturedRequest.getCreatedAt());

    verify(notificationService).saveNotification(notificationDtoCaptor.capture());
    NotificationDto capturedNotification = notificationDtoCaptor.getValue();

    assertEquals(NotificationType.MEMBERSHIP_REQUEST, capturedNotification.getType());
    assertEquals(householdOwner.getId(), capturedNotification.getRecipientId());
    assertFalse(capturedNotification.isRead());
    assertTrue(capturedNotification.getMessage().contains(testUser.getFullName()));

    verify(notificationService).broadcastNotification(any(NotificationDto.class));
  }

  @Test
  void sendJoinRequest_shouldThrowException_whenHouseholdNotFound() {
    // Arrange
    when(authentication.getName()).thenReturn(testUser.getEmail());
    when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
    when(householdRepository.findById("nonexistent-household")).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      membershipRequestService.sendJoinRequest("nonexistent-household");
    });

    assertTrue(exception.getMessage().contains("Household not found"));
    verify(membershipRequestRepository, never()).save(any(MembershipRequest.class));
  }

  @Test
  void acceptJoinRequest_shouldUpdateStatusAndAddUserToHousehold() {
    // Arrange
    when(membershipRequestRepository.findById(testJoinRequest.getId())).thenReturn(
        Optional.of(testJoinRequest));

    // Act
    membershipRequestService.acceptJoinRequest(testJoinRequest.getId());

    // Assert
    assertEquals(RequestStatus.ACCEPTED, testJoinRequest.getStatus());
    verify(membershipRequestRepository).save(testJoinRequest);

    verify(householdService).addUserToHousehold(assignmentCaptor.capture());
    UserHouseholdAssignmentRequestDto capturedAssignment = assignmentCaptor.getValue();
    assertEquals(testUser.getId(), capturedAssignment.getUserId());
  }

  @Test
  void acceptJoinRequest_shouldThrowException_whenRequestNotFound() {
    // Arrange
    when(membershipRequestRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      membershipRequestService.acceptJoinRequest(999L);
    });

    assertTrue(exception.getMessage().contains("Request not found"));
    verify(householdService, never()).addUserToHousehold(any());
  }

  @Test
  void acceptJoinRequest_shouldThrowException_whenRequestNotPending() {
    // Arrange
    MembershipRequest nonPendingRequest = new MembershipRequest();
    nonPendingRequest.setStatus(RequestStatus.REJECTED);
    when(membershipRequestRepository.findById(5L)).thenReturn(Optional.of(nonPendingRequest));

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      membershipRequestService.acceptJoinRequest(5L);
    });

    assertTrue(exception.getMessage().contains("not pending"));
    verify(householdService, never()).addUserToHousehold(any());
  }

  @Test
  void acceptInvitationRequest_shouldUpdateStatusAndAddUserToHousehold() {
    // Arrange
    when(membershipRequestRepository.findById(testInvitation.getId())).thenReturn(
        Optional.of(testInvitation));

    // Act
    membershipRequestService.acceptInvitationRequest(testInvitation.getId());

    // Assert
    assertEquals(RequestStatus.ACCEPTED, testInvitation.getStatus());
    verify(membershipRequestRepository).save(testInvitation);

    verify(householdService).addUserToHousehold(assignmentCaptor.capture());
    UserHouseholdAssignmentRequestDto capturedAssignment = assignmentCaptor.getValue();
    assertEquals(testUser.getId(), capturedAssignment.getUserId());
    assertEquals(testHousehold.getId(), capturedAssignment.getHouseholdId());
  }

  @Test
  void cancelRequest_shouldUpdateRequestStatus() {
    // Arrange
    Long requestId = 1L;
    when(membershipRequestRepository.existsById(requestId)).thenReturn(true);

    // Act
    membershipRequestService.cancelRequest(requestId);

    // Assert
    verify(membershipRequestRepository).updateStatusById(requestId, RequestStatus.CANCELED);
  }

  @Test
  void cancelRequest_shouldThrowException_whenRequestNotFound() {
    // Arrange
    Long requestId = 999L;
    when(membershipRequestRepository.existsById(requestId)).thenReturn(false);

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      membershipRequestService.cancelRequest(requestId);
    });

    assertTrue(exception.getMessage().contains("not found"));
    verify(membershipRequestRepository, never()).updateStatusById(any(), any());
  }

  @Test
  void declineRequest_shouldUpdateRequestStatus() {
    // Arrange
    Long requestId = 1L;
    when(membershipRequestRepository.existsById(requestId)).thenReturn(true);

    // Act
    membershipRequestService.declineRequest(requestId);

    // Assert
    verify(membershipRequestRepository).updateStatusById(requestId, RequestStatus.REJECTED);
  }

  @Test
  void getReceivedInvitationsByUser_shouldReturnAllInvitationsForUser() {
    // Arrange
    when(authentication.getName()).thenReturn(testUser.getEmail());
    when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
    when(membershipRequestRepository.findAllByReceiverAndTypeAndStatus(
        testUser, RequestType.INVITATION, RequestStatus.PENDING))
        .thenReturn(List.of(testInvitation));

    // Act
    List<MembershipRequestResponseDto> result
        = membershipRequestService.getReceivedInvitationsByUser();

    // Assert
    assertEquals(1, result.size());
    assertEquals(testInvitation.getId(), result.get(0).getId());
    assertEquals(testHousehold.getId(), result.get(0).getHouseholdId());
    assertEquals(householdOwner.getId(), result.get(0).getSender().getId());
    assertEquals(testUser.getId(), result.get(0).getRecipient().getId());
    assertEquals(RequestType.INVITATION, result.get(0).getRequestType());
  }

  @Test
  void getReceivedJoinRequestsByHousehold_shouldReturnAllJoinRequestsForHousehold() {
    // Arrange
    when(authentication.getName()).thenReturn(householdOwner.getEmail());
    when(userRepository.findByEmail(householdOwner.getEmail())).thenReturn(
        Optional.of(householdOwner));
    when(membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatus(
        testHousehold.getId(), RequestType.JOIN_REQUEST, RequestStatus.PENDING))
        .thenReturn(List.of(testJoinRequest));

    // Act
    List<MembershipRequestResponseDto> result
        = membershipRequestService.getReceivedJoinRequestsByHousehold();

    // Assert
    assertEquals(1, result.size());
    assertEquals(testJoinRequest.getId(), result.get(0).getId());
    assertEquals(testHousehold.getId(), result.get(0).getHouseholdId());
    assertEquals(testUser.getId(), result.get(0).getSender().getId());
    assertEquals(householdOwner.getId(), result.get(0).getRecipient().getId());
    assertEquals(RequestStatus.PENDING, result.get(0).getStatus());
  }

  @Test
  void getAcceptedReceivedJoinRequestsByHousehold_shouldReturnAcceptedRequests() {
    // Arrange
    when(authentication.getName()).thenReturn(householdOwner.getEmail());
    when(userRepository.findByEmail(householdOwner.getEmail())).thenReturn(
        Optional.of(householdOwner));

    // Create an accepted join request
    MembershipRequest acceptedRequest = new MembershipRequest();
    acceptedRequest.setId(3L);
    acceptedRequest.setHousehold(testHousehold);
    acceptedRequest.setSender(testUser);
    acceptedRequest.setReceiver(householdOwner);
    acceptedRequest.setType(RequestType.JOIN_REQUEST);
    acceptedRequest.setStatus(RequestStatus.ACCEPTED);
    acceptedRequest.setCreatedAt(Timestamp.from(Instant.now()));

    List<MembershipRequest> acceptedRequests = List.of(acceptedRequest);
    when(membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatus(
        testHousehold.getId(), RequestType.JOIN_REQUEST, RequestStatus.ACCEPTED))
        .thenReturn(acceptedRequests);

    // Act
    List<MembershipRequestResponseDto> result
        = membershipRequestService.getAcceptedReceivedJoinRequestsByHousehold();

    // Assert
    assertEquals(1, result.size());
    assertEquals(acceptedRequest.getId(), result.get(0).getId());
    assertEquals(RequestStatus.ACCEPTED, result.get(0).getStatus());
  }

  @Test
  void getInvitationsSentByHousehold_shouldReturnAllInvitationsForHousehold() {
    // Arrange
    when(authentication.getName()).thenReturn(householdOwner.getEmail());
    when(userRepository.findByEmail(householdOwner.getEmail())).thenReturn(
        Optional.of(householdOwner));
    when(membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatusIn(
        eq(testHousehold.getId()), eq(RequestType.INVITATION), anyList()))
        .thenReturn(List.of(testInvitation));

    // Act
    List<MembershipRequestResponseDto> result
        = membershipRequestService.getInvitationsSentByHousehold();

    // Assert
    assertEquals(1, result.size());
    assertEquals(testInvitation.getId(), result.get(0).getId());
    assertEquals(testHousehold.getId(), result.get(0).getHouseholdId());
    assertEquals(householdOwner.getId(), result.get(0).getSender().getId());
    assertEquals(testUser.getId(), result.get(0).getRecipient().getId());
    assertEquals(RequestType.INVITATION, result.get(0).getRequestType());
  }
}