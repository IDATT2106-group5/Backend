package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.MembershipRequest;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MembershipRequestRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link MembershipRequestService} class.
 * This test class uses Mockito to mock dependencies and validate the behavior of the service methods.
 */
class MembershipRequestServiceTest {

  private MembershipRequestRepository membershipRequestRepository;
  private HouseholdRepository householdRepository;
  private UserRepository userRepository;
  private MembershipRequestService service;

  /**
   * Sets up the test environment by initializing mocks and the service instance.
   */
  @BeforeEach
  void setUp() {
    membershipRequestRepository = mock(MembershipRequestRepository.class);
    householdRepository = mock(HouseholdRepository.class);
    userRepository = mock(UserRepository.class);
    service = new MembershipRequestService(membershipRequestRepository, householdRepository,
        userRepository);
  }

  /**
   * Tests for the sendInvitation method in {@link MembershipRequestService}.
   */
  @Nested
  class SendInvitationTests {

    /**
     * Verifies that sending an invitation with valid data succeeds.
     */
    @Test
    void sendInvitationWithValidDataSucceeds() {
      MembershipRequestDto dto = new MembershipRequestDto();
      dto.setUserEmail("user@example.com");
      dto.setHouseholdName("House A");
      User receiver = new User();
      Household household = new Household();
      household.setOwner(new User());
      when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
      when(householdRepository.existsByName("House A")).thenReturn(true);
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(receiver));
      when(householdRepository.findByName("House A")).thenReturn(Optional.of(household));

      service.sendInvitation(dto);

      verify(membershipRequestRepository, times(1)).save(any(MembershipRequest.class));
    }

    /**
     * Verifies that sending an invitation throws an exception when the user does not exist.
     */
    @Test
    void sendInvitationThrowsExceptionForNonexistentUser() {
      MembershipRequestDto dto = new MembershipRequestDto();
      dto.setUserEmail("nonexistent@example.com");
      dto.setHouseholdName("House A");
      when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

      Exception exception =
          assertThrows(IllegalArgumentException.class, () -> service.sendInvitation(dto));
      assertEquals("User not found", exception.getMessage());
    }

    /**
     * Verifies that sending an invitation throws an exception when the household does not exist.
     */
    @Test
    void sendInvitationThrowsExceptionForNonexistentHousehold() {
      MembershipRequestDto dto = new MembershipRequestDto();
      dto.setUserEmail("user@example.com");
      dto.setHouseholdName("Nonexistent House");
      when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
      when(householdRepository.existsByName("Nonexistent House")).thenReturn(false);

      Exception exception =
          assertThrows(IllegalArgumentException.class, () -> service.sendInvitation(dto));
      assertEquals("Household not found", exception.getMessage());
    }
  }

  /**
   * Tests for the sendJoinRequest method in {@link MembershipRequestService}.
   */
  @Nested
  class SendJoinRequestTests {

    /**
     * Verifies that sending a join request with valid data succeeds.
     */
    @Test
    void sendJoinRequestWithValidDataSucceeds() {
      MembershipRequestDto dto = new MembershipRequestDto();
      dto.setUserEmail("user@example.com");
      dto.setHouseholdName("House A");
      User sender = new User();
      Household household = new Household();
      household.setOwner(new User());
      when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
      when(householdRepository.existsByName("House A")).thenReturn(true);
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sender));
      when(householdRepository.findByName("House A")).thenReturn(Optional.of(household));

      service.sendJoinRequest(dto);

      verify(membershipRequestRepository, times(1)).save(any(MembershipRequest.class));
    }

    /**
     * Verifies that sending a join request throws an exception when the user does not exist.
     */
    @Test
    void sendJoinRequestThrowsExceptionForNonexistentUser() {
      MembershipRequestDto dto = new MembershipRequestDto();
      dto.setUserEmail("nonexistent@example.com");
      dto.setHouseholdName("House A");
      when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

      Exception exception =
          assertThrows(IllegalArgumentException.class, () -> service.sendJoinRequest(dto));
      assertEquals("User not found", exception.getMessage());
    }

    /**
     * Verifies that sending a join request throws an exception when the household does not exist.
     */
    @Test
    void sendJoinRequestThrowsExceptionForNonexistentHousehold() {
      MembershipRequestDto dto = new MembershipRequestDto();
      dto.setUserEmail("user@example.com");
      dto.setHouseholdName("Nonexistent House");
      when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
      when(householdRepository.existsByName("Nonexistent House")).thenReturn(false);

      Exception exception =
          assertThrows(IllegalArgumentException.class, () -> service.sendJoinRequest(dto));
      assertEquals("Household not found", exception.getMessage());
    }
  }

  /**
   * Tests for the acceptRequest method in {@link MembershipRequestService}.
   */
  @Nested
  class AcceptRequestTests {

    /**
     * Verifies that accepting a request with a valid request ID succeeds.
     */
    @Test
    void acceptRequestSucceedsWithValidRequestId() {
      Long requestId = 1L;
      when(membershipRequestRepository.existsById(requestId)).thenReturn(true);

      service.acceptRequest(requestId);

      verify(membershipRequestRepository, times(1)).updateStatusById(requestId,
          RequestStatus.ACCEPTED);
    }

    /**
     * Verifies that accepting a request throws an exception when the request does not exist.
     */
    @Test
    void acceptRequestThrowsExceptionForNonexistentRequest() {
      Long requestId = 999L;
      when(membershipRequestRepository.existsById(requestId)).thenReturn(false);

      Exception exception =
          assertThrows(IllegalArgumentException.class, () -> service.acceptRequest(requestId));
      assertEquals("Request not found", exception.getMessage());
    }
  }

  /**
   * Tests for the declineRequest method in {@link MembershipRequestService}.
   */
  @Nested
  class DeclineRequestTests {

    /**
     * Verifies that declining a request with a valid request ID succeeds.
     */
    @Test
    void declineRequestSucceedsWithValidRequestId() {
      Long requestId = 1L;
      when(membershipRequestRepository.existsById(requestId)).thenReturn(true);

      service.declineRequest(requestId);

      verify(membershipRequestRepository, times(1)).updateStatusById(requestId,
          RequestStatus.REJECTED);
    }

    /**
     * Verifies that declining a request throws an exception when the request does not exist.
     */
    @Test
    void declineRequestThrowsExceptionForNonexistentRequest() {
      Long requestId = 999L;
      when(membershipRequestRepository.existsById(requestId)).thenReturn(false);

      Exception exception =
          assertThrows(IllegalArgumentException.class, () -> service.declineRequest(requestId));
      assertEquals("Request not found", exception.getMessage());
    }
  }

  /**
   * Tests for the getActiveRequestsByUser method in {@link MembershipRequestService}.
   */
  @Nested
  class GetActiveRequestsByUserTests {

    /**
     * Verifies that retrieving active requests for a valid user returns the expected list.
     */
    @Test
    void getActiveRequestsByUserReturnsListForValidUser() {
      String email = "user@example.com";
      User user = new User();
      List<MembershipRequest> activeRequests = Collections.singletonList(new MembershipRequest());
      when(userRepository.existsByEmail(email)).thenReturn(true);
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(membershipRequestRepository.findAllByReceiverAndStatus(user, RequestStatus.PENDING))
          .thenReturn(activeRequests);

      List<MembershipRequest> result = service.getActiveRequestsByUser(email);
      assertEquals(activeRequests, result);
    }

    /**
     * Verifies that retrieving active requests throws an exception when the user does not exist.
     */
    @Test
    void getActiveRequestsByUserThrowsExceptionForNonexistentUser() {
      String email = "nonexistent@example.com";
      when(userRepository.existsByEmail(email)).thenReturn(false);

      Exception exception = assertThrows(IllegalArgumentException.class,
          () -> service.getActiveRequestsByUser(email));
      assertEquals("User not found", exception.getMessage());
    }
  }
}