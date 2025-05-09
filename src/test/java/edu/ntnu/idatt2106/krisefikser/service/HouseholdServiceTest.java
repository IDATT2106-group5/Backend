package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.household.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.EditHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdBasicResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.UnregisteredHouseholdMember;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UnregisteredHouseholdMemberRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class HouseholdServiceTest {

  @Mock
  private HouseholdRepository householdRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UnregisteredHouseholdMemberRepository unregisteredHouseholdMemberRepository;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private HouseholdService householdService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class CreateHouseholdTests {

    @Test
    void shouldCreateHouseholdSuccessfully() {
      // Setup
      CreateHouseholdRequestDto request = new CreateHouseholdRequestDto();
      request.setName("Test Household");
      request.setAddress("Test Address");

      User owner = new User();
      owner.setId("user123");
      owner.setEmail("test@example.com");

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(owner));

      // Execute
      householdService.createHousehold(request);

      // Verify
      verify(householdRepository).save(any(Household.class));
      verify(userRepository).updateHouseholdId(eq("user123"), anyString());
      verify(notificationService).saveNotification(any(NotificationDto.class));
      verify(notificationService).sendPrivateNotification(eq("user123"),
          any(NotificationDto.class));
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
      // Setup
      CreateHouseholdRequestDto request = new CreateHouseholdRequestDto();
      request.setName("Test Household");
      request.setAddress("Test Address");

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

      // Execute & Verify
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.createHousehold(request));
      assertEquals("User not found", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }

    @Test
    void shouldGenerateUniqueHouseholdId() {
      // Setup
      CreateHouseholdRequestDto request = new CreateHouseholdRequestDto();
      request.setName("Test Household");
      request.setAddress("Test Address");

      User owner = new User();
      owner.setId("user123");
      owner.setEmail("test@example.com");

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(owner));

      // Execute
      householdService.createHousehold(request);

      // Verify
      verify(householdRepository).save(argThat(household ->
          household.getId() != null
              && household.getId().length() == 8
              && household.getName().equals("Test Household")
              && household.getAddress().equals("Test Address")
      ));
    }

    @Test
    void shouldCreateAndSendNotification() {
      // Setup
      CreateHouseholdRequestDto request = new CreateHouseholdRequestDto();
      request.setName("Test Household");
      request.setAddress("Test Address");

      User owner = new User();
      owner.setId("user123");
      owner.setEmail("test@example.com");

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(owner));

      // Execute
      householdService.createHousehold(request);

      // Verify
      verify(notificationService).saveNotification(argThat(notification ->
          notification.getMessage().equals("Household created successfully")
              && notification.getType() == NotificationType.HOUSEHOLD
              && notification.getRecipientId().equals("user123")
              && !notification.isRead()
      ));

      verify(notificationService).sendPrivateNotification(eq("user123"),
          any(NotificationDto.class));
    }
  }

  @Nested
  class AddUserToHouseholdTests {

    @Test
    void shouldAddUserToHouseholdSuccessfully() {
      User user = new User();
      user.setId("user123");
      user.setFullName("Test User");

      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setNumberOfMembers(1);

      User currentUser = new User();
      currentUser.setId("owner123");
      currentUser.setEmail("owner@example.com");
      currentUser.setHousehold(household);

      household.setOwner(currentUser);

      UserHouseholdAssignmentRequestDto request = new UserHouseholdAssignmentRequestDto();
      request.setUserId("user123");
      request.setHouseholdId("household123");

      when(userRepository.findById("user123")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));
      when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(currentUser));

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("owner@example.com");
      SecurityContextHolder.setContext(securityContext);

      householdService.addUserToHousehold(request);

      verify(userRepository).updateHouseholdId("user123", "household123");
      verify(householdRepository).updateNumberOfMembers("household123", 2);
      verify(notificationService).saveHouseholdNotification(any(NotificationDto.class),
          eq("household123"));
      verify(notificationService).sendPrivateNotification(eq("user123"),
          any(NotificationDto.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
      UserHouseholdAssignmentRequestDto request = new UserHouseholdAssignmentRequestDto();
      request.setUserId("user123");
      request.setHouseholdId("household123");

      when(userRepository.findById("user123")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUserToHousehold(request));
      assertEquals("User not found", exception.getMessage());

      verify(householdRepository, never()).findById(anyString());
      verify(userRepository, never()).updateHouseholdId(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenHouseholdNotFound() {
      User user = new User();
      user.setId("user123");

      UserHouseholdAssignmentRequestDto request = new UserHouseholdAssignmentRequestDto();
      request.setUserId("user123");
      request.setHouseholdId("household123");

      when(userRepository.findById("user123")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUserToHousehold(request));
      assertEquals("Household not found", exception.getMessage());

      verify(userRepository, never()).updateHouseholdId(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyInHousehold() {
      User user = new User();
      user.setId("user123");

      Household household = new Household();
      household.setId("household123");

      user.setHousehold(household);

      UserHouseholdAssignmentRequestDto request = new UserHouseholdAssignmentRequestDto();
      request.setUserId("user123");
      request.setHouseholdId("household123");

      when(userRepository.findById("user123")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUserToHousehold(request));
      assertEquals("User is already a member of this household", exception.getMessage());

      verify(userRepository, never()).updateHouseholdId(anyString(), anyString());
    }
  }

  @Nested
  class RemoveUserFromHouseholdTests {

    @Test
    void shouldRemoveUserFromHouseholdSuccessfully() {
      User user = new User();
      user.setId("user123");
      user.setFullName("Test User");

      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setNumberOfMembers(2);

      user.setHousehold(household);

      User currentUser = new User();
      currentUser.setId("owner123");
      currentUser.setEmail("owner@example.com");
      currentUser.setHousehold(household);

      when(userRepository.findById("user123")).thenReturn(Optional.of(user));
      when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(currentUser));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("owner@example.com");
      SecurityContextHolder.setContext(securityContext);

      householdService.removeUserFromHousehold("user123");

      verify(userRepository).updateHouseholdId("user123", null);
      verify(householdRepository).updateNumberOfMembers("household123", 1);
      verify(notificationService).saveHouseholdNotification(any(NotificationDto.class),
          eq("household123"));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
      when(userRepository.findById("user123")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.removeUserFromHousehold("user123"));
      assertEquals("User not found", exception.getMessage());

      verify(householdRepository, never()).findById(anyString());
      verify(userRepository, never()).updateHouseholdId(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserNotInHousehold() {
      User user = new User();
      user.setId("user123");

      when(userRepository.findById("user123")).thenReturn(Optional.of(user));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.removeUserFromHousehold("user123"));
      assertEquals("User is not a member of any household", exception.getMessage());

      verify(householdRepository, never()).findById(anyString());
      verify(userRepository, never()).updateHouseholdId(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserNotInCurrentHousehold() {
      User user = new User();
      user.setId("user123");

      Household household = new Household();
      household.setId("household123");

      user.setHousehold(household);

      Household currentHousehold = new Household();
      currentHousehold.setId("household456");

      User currentUser = new User();
      currentUser.setId("owner123");
      currentUser.setEmail("owner@example.com");
      currentUser.setHousehold(currentHousehold);

      when(userRepository.findById("user123")).thenReturn(Optional.of(user));
      when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(currentUser));
      when(householdRepository.findById("household456")).thenReturn(Optional.of(currentHousehold));

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("owner@example.com");
      SecurityContextHolder.setContext(securityContext);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.removeUserFromHousehold("user123"));
      assertEquals("User is not a member of this household", exception.getMessage());

      verify(userRepository, never()).updateHouseholdId(anyString(), anyString());
    }
  }

  @Nested
  class LeaveCurrentUserFromHouseholdTests {

    @Test
    void shouldLeaveHouseholdSuccessfully() {
      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");
      user.setFullName("Test User");

      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setNumberOfMembers(2);

      User owner = new User();
      owner.setId("owner123");
      household.setOwner(owner);
      user.setHousehold(household);

      // Mock UserDetails in addition to Authentication
      UserDetails userDetails = mock(UserDetails.class);
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      when(userDetails.getUsername()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      householdService.leaveCurrentUserFromHousehold();

      verify(userRepository).updateHouseholdId("user123", null);
      verify(householdRepository).updateNumberOfMembers("household123", 1);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
      // Mock UserDetails in addition to Authentication
      UserDetails userDetails = mock(UserDetails.class);
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      when(userDetails.getUsername()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.leaveCurrentUserFromHousehold());
      assertEquals("Authenticated user not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotInHousehold() {
      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");
      user.setFullName("Test User");

      // Mock UserDetails in addition to Authentication
      UserDetails userDetails = mock(UserDetails.class);
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      when(userDetails.getUsername()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.leaveCurrentUserFromHousehold());
      assertEquals("You are not a member of any household.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIsOwner() {
      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");
      user.setFullName("Test User");

      Household household = new Household();
      household.setId("household123");
      household.setOwner(user);
      user.setHousehold(household);

      // Mock UserDetails in addition to Authentication
      UserDetails userDetails = mock(UserDetails.class);
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      when(userDetails.getUsername()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.leaveCurrentUserFromHousehold());
      assertEquals("Owner cannot leave the household. Transfer ownership first.",
          exception.getMessage());
    }
  }

  @Nested
  class AddUnregisteredMemberToHouseholdTests {

    @Test
    void shouldAddUnregisteredMemberToHouseholdSuccessfully() {
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          new UnregisteredMemberHouseholdAssignmentRequestDto();
      request.setFullName("John Doe");

      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");

      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setNumberOfMembers(1);
      user.setHousehold(household);

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

      householdService.addUnregisteredMemberToHousehold(request);

      verify(unregisteredHouseholdMemberRepository).save(argThat(member ->
          member.getFullName().equals("John Doe")
              && member.getHousehold().getId().equals("household123")
      ));
      verify(householdRepository).updateNumberOfMembers("household123", 2);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          new UnregisteredMemberHouseholdAssignmentRequestDto();
      request.setFullName("John Doe");

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUnregisteredMemberToHousehold(request));
      assertEquals("User not found", exception.getMessage());

      verify(unregisteredHouseholdMemberRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotInHousehold() {
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          new UnregisteredMemberHouseholdAssignmentRequestDto();
      request.setFullName("John Doe");

      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUnregisteredMemberToHousehold(request));
      assertEquals("User does not belong to a household", exception.getMessage());

      verify(unregisteredHouseholdMemberRepository, never()).save(any());
    }
  }

  @Nested
  class RemoveUnregisteredMemberFromHouseholdTests {

    @Test
    void shouldRemoveUnregisteredMemberSuccessfully() {
      UnregisteredHouseholdMember member = new UnregisteredHouseholdMember();
      member.setId(1L);
      member.setFullName("John Doe");

      Household household = new Household();
      household.setId("household123");
      household.setNumberOfMembers(2);
      member.setHousehold(household);

      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");
      user.setHousehold(household);

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(unregisteredHouseholdMemberRepository.findById(1L)).thenReturn(Optional.of(member));

      householdService.removeUnregisteredMemberFromHousehold(1L);

      verify(unregisteredHouseholdMemberRepository).delete(member);
      verify(householdRepository).updateNumberOfMembers("household123", 1);
    }

    @Test
    void shouldThrowExceptionWhenUnregisteredMemberNotFound() {
      when(unregisteredHouseholdMemberRepository.findById(1L)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.removeUnregisteredMemberFromHousehold(1L));
      assertEquals("Unregistered member not found", exception.getMessage());

      verify(unregisteredHouseholdMemberRepository, never()).delete(any());
      verify(householdRepository, never()).updateNumberOfMembers(anyString(), anyInt());
    }

    @Test
    void shouldThrowExceptionWhenUserNotAuthorizedToRemoveMember() {
      UnregisteredHouseholdMember member = new UnregisteredHouseholdMember();
      member.setId(1L);
      member.setFullName("John Doe");

      Household household = new Household();
      household.setId("household123");
      member.setHousehold(household);

      Household otherHousehold = new Household();
      otherHousehold.setId("household456");

      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");
      user.setHousehold(otherHousehold);

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(unregisteredHouseholdMemberRepository.findById(1L)).thenReturn(Optional.of(member));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.removeUnregisteredMemberFromHousehold(1L));
      assertEquals("You are not authorized to remove this member", exception.getMessage());

      verify(unregisteredHouseholdMemberRepository, never()).delete(any());
      verify(householdRepository, never()).updateNumberOfMembers(anyString(), anyInt());
    }
  }

  @Nested
  class ChangeHouseholdOwnerTests {

    @Test
    void shouldChangeHouseholdOwnerSuccessfully() {
      // Create Household first
      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setNumberOfMembers(2);

      // Create users
      User currentUser = new User();
      currentUser.setId("currentOwner123");
      currentUser.setEmail("current@example.com");
      currentUser.setFullName("Current Owner");
      currentUser.setHousehold(household); // Set the household

      User newOwner = new User();
      newOwner.setId("newOwner123");
      newOwner.setFullName("New Owner");
      newOwner.setHousehold(household); // Set the household

      // Set current user as owner
      household.setOwner(currentUser);

      UserHouseholdAssignmentRequestDto request = new UserHouseholdAssignmentRequestDto();
      request.setUserId("newOwner123");

      when(userRepository.findById("newOwner123")).thenReturn(Optional.of(newOwner));
      when(userRepository.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("current@example.com");
      SecurityContextHolder.setContext(securityContext);

      householdService.changeHouseholdOwner(request);

      verify(householdRepository).save(argThat(h -> h.getOwner().equals(newOwner)));
      verify(notificationService).saveNotification(any(NotificationDto.class));
      verify(notificationService).sendPrivateNotification(eq("newOwner123"),
          any(NotificationDto.class));
    }

    @Test
    void shouldThrowExceptionWhenCurrentUserNotFound() {
      UserHouseholdAssignmentRequestDto request = new UserHouseholdAssignmentRequestDto();
      request.setUserId("newOwner123");

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("current@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("current@example.com")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.changeHouseholdOwner(request));
      assertEquals("User not found", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }

    @Test
    void shouldThrowExceptionWhenCurrentUserNotOwner() {
      // Create Household first
      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");

      // Create another user as the owner
      User otherOwner = new User();
      otherOwner.setId("otherOwner123");
      otherOwner.setEmail("other@example.com");
      household.setOwner(otherOwner); // This sets the owner correctly

      // Create current user (non-owner)
      User currentUser = new User();
      currentUser.setId("currentUser123");
      currentUser.setEmail("current@example.com");
      currentUser.setHousehold(household); // Same household but not owner

      // Create new owner
      User newOwner = new User();
      newOwner.setId("newOwner123");
      newOwner.setHousehold(household);

      UserHouseholdAssignmentRequestDto request = new UserHouseholdAssignmentRequestDto();
      request.setUserId("newOwner123");
      request.setHouseholdId("household123");

      when(userRepository.findById("newOwner123")).thenReturn(Optional.of(newOwner));
      when(userRepository.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("current@example.com");
      SecurityContextHolder.setContext(securityContext);

      // Expect an IllegalArgumentException with specific message
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class,
          () -> householdService.changeHouseholdOwner(request),
          "Expected IllegalArgumentException to be thrown"
      );

      assertEquals("Only the owner can transfer ownership", exception.getMessage());
      verify(householdRepository, never()).save(any(Household.class));
    }

    @Test
    void shouldThrowExceptionWhenNewOwnerNotFound() {
      UserHouseholdAssignmentRequestDto request = new UserHouseholdAssignmentRequestDto();
      request.setUserId("newOwner123");
      request.setHouseholdId("household123"); // Add this line to ensure householdId is set

      when(userRepository.findById("newOwner123")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.changeHouseholdOwner(request));
      assertEquals("User not found", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }
  }

  @Nested
  class SearchHouseholdByIdTests {

    @Test
    void searchHouseholdByIdReturnsHouseholdSuccessfully() {
      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");

      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      HouseholdBasicResponseDto result = householdService.searchHouseholdById("household123");

      assertEquals("household123", result.getId());
      assertEquals("Test Household", result.getName());
    }

    @Test
    void searchHouseholdByIdThrowsExceptionWhenHouseholdNotFound() {
      when(householdRepository.findById("household123")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.searchHouseholdById("household123"));

      assertEquals("Household not found", exception.getMessage());
    }
  }

  @Nested
  class EditHouseholdTests {

    @Test
    void editHouseholdSuccessfullyUpdatesNameAndAddress() {
      EditHouseholdRequestDto request = new EditHouseholdRequestDto();
      request.setName("Updated Household Name");
      request.setAddress("Updated Address");

      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");

      Household household = new Household();
      household.setId("household123");
      household.setName("Old Household Name");
      household.setAddress("Old Address");
      household.setOwner(user);

      user.setHousehold(household);

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      householdService.editHousehold(request);

      verify(householdRepository).save(argThat(h ->
          h.getName().equals("Updated Household Name")
              && h.getAddress().equals("Updated Address")
      ));
      verify(notificationService).saveNotification(any(NotificationDto.class));
      verify(notificationService).sendPrivateNotification(eq("user123"),
          any(NotificationDto.class));
    }

    @Test
    void editHouseholdThrowsExceptionWhenUserNotFound() {
      EditHouseholdRequestDto request = new EditHouseholdRequestDto();
      request.setName("Updated Household Name");
      request.setAddress("Updated Address");

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.editHousehold(request));
      assertEquals("No user logged in", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }

    @Test
    void editHouseholdThrowsExceptionWhenHouseholdNotFound() {
      EditHouseholdRequestDto request = new EditHouseholdRequestDto();
      request.setName("Updated Household Name");
      request.setAddress("Updated Address");

      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");
      // Create a household to avoid NPE when trying to get id
      Household household = new Household();
      household.setId("household123");
      user.setHousehold(household);

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.editHousehold(request));
      assertEquals("Household not found", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }

    @Test
    void editHouseholdThrowsExceptionWhenUserNotOwner() {
      EditHouseholdRequestDto request = new EditHouseholdRequestDto();
      request.setName("Updated Household Name");
      request.setAddress("Updated Address");

      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");

      User owner = new User();
      owner.setId("owner123");

      Household household = new Household();
      household.setId("household123");
      household.setOwner(owner);

      user.setHousehold(household);

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.editHousehold(request));
      assertEquals("Only the owner can edit the household", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }

  }

  @Nested
  class DeleteHouseholdTests {

    @Test
    void deleteHouseholdSuccessfullyRemovesHouseholdAndAssociatedEntities() {
      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");

      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setOwner(user);

      user.setHousehold(household);

      UnregisteredHouseholdMember member = new UnregisteredHouseholdMember();
      member.setId(1L);
      member.setHousehold(household);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));
      when(userRepository.getUsersByHousehold(household)).thenReturn(List.of(user));
      when(unregisteredHouseholdMemberRepository.findUnregisteredHouseholdMembersByHousehold(
          household))
          .thenReturn(List.of(member));

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      householdService.deleteHousehold();

      // Update this verification to match the actual implementation
      verify(userRepository).save(user);
      verify(unregisteredHouseholdMemberRepository).deleteAll(List.of(member));
      verify(householdRepository).delete(household);
      verify(notificationService).saveHouseholdNotification(any(NotificationDto.class),
          eq("household123"));
    }

    @Test
    void deleteHouseholdThrowsExceptionWhenHouseholdNotFound() {
      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");

      // Create a household to avoid NPE when trying to get id
      Household household = new Household();
      household.setId("household123");
      user.setHousehold(household);

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.deleteHousehold());
      assertEquals("Household not found", exception.getMessage());

      verify(householdRepository, never()).delete(any(Household.class));
    }

    @Test
    void deleteHouseholdThrowsExceptionWhenUserNotFound() {
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.deleteHousehold());
      assertEquals("User not found", exception.getMessage());

      verify(householdRepository, never()).delete(any(Household.class));
    }

    @Test
    void deleteHouseholdThrowsExceptionWhenUserNotOwner() {
      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");

      User owner = new User();
      owner.setId("owner123");

      Household household = new Household();
      household.setId("household123");
      household.setOwner(owner);

      user.setHousehold(household);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.deleteHousehold());
      assertEquals("Only the owner can delete the household", exception.getMessage());

      verify(householdRepository, never()).delete(any(Household.class));
    }

  }

  @Nested
  class GetHouseholdTests {

    @Test
    void getHouseholdReturnsHouseholdSuccessfully() {
      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setAddress("123 Test Street");

      User owner = new User();
      owner.setId("owner123");
      owner.setEmail("owner@example.com");
      owner.setFullName("Owner Name");
      owner.setTlf("12345678");
      owner.setRole(Role.USER);
      household.setOwner(owner);

      when(householdRepository.findById("household123")).thenReturn(Optional.of(household));

      HouseholdResponseDto result = householdService.getHousehold("household123");

      assertEquals("household123", result.getId());
      assertEquals("Test Household", result.getName());
      assertEquals("123 Test Street", result.getAddress());
      assertEquals("owner123", result.getOwner().getId());
      assertEquals("owner@example.com", result.getOwner().getEmail());
      assertEquals("Owner Name", result.getOwner().getFullName());
      assertEquals("12345678", result.getOwner().getTlf());
      assertEquals(Role.USER, result.getOwner().getRole());
    }

    @Test
    void getHouseholdThrowsExceptionWhenHouseholdNotFound() {
      when(householdRepository.findById("household123")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.getHousehold("household123"));
      assertEquals("Household not found", exception.getMessage());
    }
  }

  @Nested
  class GetHouseholdDetailsTests {

    @Test
    void successfullyReturnsHouseholdDetails() {
      // Setup
      User owner = new User();
      owner.setId("owner123");
      owner.setEmail("owner@example.com");
      owner.setFullName("Owner User");
      owner.setTlf("12345678");
      owner.setRole(Role.USER);

      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setAddress("123 Main St");
      household.setOwner(owner);

      User currentUser = new User();
      currentUser.setId("user123");
      currentUser.setEmail("test@example.com");
      currentUser.setFullName("Test User");
      currentUser.setTlf("87654321");
      currentUser.setRole(Role.USER);
      currentUser.setHousehold(household);

      User anotherUser = new User();
      anotherUser.setId("user456");
      anotherUser.setEmail("another@example.com");
      anotherUser.setFullName("Another User");
      anotherUser.setTlf("13579246");
      anotherUser.setRole(Role.USER);
      anotherUser.setHousehold(household);

      UnregisteredHouseholdMember unregisteredMember1 = new UnregisteredHouseholdMember();
      unregisteredMember1.setId(1L);
      unregisteredMember1.setFullName("Unregistered 1");
      unregisteredMember1.setHousehold(household);

      UnregisteredHouseholdMember unregisteredMember2 = new UnregisteredHouseholdMember();
      unregisteredMember2.setId(2L);
      unregisteredMember2.setFullName("Unregistered 2");
      unregisteredMember2.setHousehold(household);

      // Mock security context
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      // Mock repository responses
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
      List<User> users = Arrays.asList(owner, currentUser, anotherUser);
      when(userRepository.getUsersByHousehold(household)).thenReturn(users);
      List<UnregisteredHouseholdMember> unregisteredMembers =
          Arrays.asList(unregisteredMember1, unregisteredMember2);
      when(unregisteredHouseholdMemberRepository.findUnregisteredHouseholdMembersByHousehold(
          household))
          .thenReturn(unregisteredMembers);

      // Execute
      Map<String, Object> result = householdService.getHouseholdDetails();

      // Verify
      assertNotNull(result);
      assertEquals(3, result.size());
      assertTrue(result.containsKey("household"));
      assertTrue(result.containsKey("users"));
      assertTrue(result.containsKey("unregisteredMembers"));

      // Verify household details
      HouseholdResponseDto householdResponse = (HouseholdResponseDto) result.get("household");
      assertEquals("household123", householdResponse.getId());
      assertEquals("Test Household", householdResponse.getName());
      assertEquals("123 Main St", householdResponse.getAddress());
      assertEquals("owner123", householdResponse.getOwner().getId());

      // Verify users list
      @SuppressWarnings("unchecked")
      List<UserResponseDto> usersList = (List<UserResponseDto>) result.get("users");
      assertEquals(3, usersList.size());
      // Verify that the owner is part of the list
      boolean foundOwner = usersList.stream()
          .anyMatch(user -> user.getId().equals("owner123"));
      assertTrue(foundOwner);
      // Verify that current user is part of the list
      boolean foundCurrentUser = usersList.stream()
          .anyMatch(user -> user.getId().equals("user123"));
      assertTrue(foundCurrentUser);

      // Verify unregistered members list
      @SuppressWarnings("unchecked")
      List<UnregisteredMemberResponseDto> unregisteredList =
          (List<UnregisteredMemberResponseDto>) result.get("unregisteredMembers");
      assertEquals(2, unregisteredList.size());
      // Verify that members are in the list
      boolean foundMember1 = unregisteredList.stream()
          .anyMatch(member -> member.getId() == 1L);
      assertTrue(foundMember1);
      boolean foundMember2 = unregisteredList.stream()
          .anyMatch(member -> member.getId() == 2L);
      assertTrue(foundMember2);
    }

    @Test
    void throwsExceptionWhenUserNotFound() {
      // Mock security context
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      // Mock repository responses
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

      // Execute & Verify
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.getHouseholdDetails());
      assertEquals("User not found", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenUserDoesNotBelongToHousehold() {
      // Setup
      User user = new User();
      user.setId("user123");
      user.setEmail("test@example.com");
      user.setFullName("Test User");
      // User has no household

      // Mock security context
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      // Mock repository responses
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

      // Execute & Verify
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.getHouseholdDetails());
      assertEquals("User does not belong to a household", exception.getMessage());
    }

    @Test
    void handlesHouseholdWithNoOwner() {
      // Setup
      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setAddress("123 Main St");
      // No owner set

      User currentUser = new User();
      currentUser.setId("user123");
      currentUser.setEmail("test@example.com");
      currentUser.setFullName("Test User");
      currentUser.setTlf("87654321");
      currentUser.setRole(Role.USER);
      currentUser.setHousehold(household);

      // Mock security context
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      // Mock repository responses
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
      List<User> users = Collections.singletonList(currentUser);
      when(userRepository.getUsersByHousehold(household)).thenReturn(users);
      List<UnregisteredHouseholdMember> unregisteredMembers = Collections.emptyList();
      when(unregisteredHouseholdMemberRepository.findUnregisteredHouseholdMembersByHousehold(
          household))
          .thenReturn(unregisteredMembers);

      // Execute
      Map<String, Object> result = householdService.getHouseholdDetails();

      // Verify
      assertNotNull(result);
      HouseholdResponseDto householdResponse = (HouseholdResponseDto) result.get("household");
      assertEquals("household123", householdResponse.getId());
      assertEquals("Test Household", householdResponse.getName());
      // Owner should be null
      assertNull(householdResponse.getOwner());
    }

    @Test
    void handlesEmptyLists() {
      // Setup
      User owner = new User();
      owner.setId("owner123");
      owner.setEmail("owner@example.com");
      owner.setFullName("Owner User");
      owner.setTlf("12345678");
      owner.setRole(Role.USER);

      Household household = new Household();
      household.setId("household123");
      household.setName("Test Household");
      household.setAddress("123 Main St");
      household.setOwner(owner);

      User currentUser = new User();
      currentUser.setId("user123");
      currentUser.setEmail("test@example.com");
      currentUser.setFullName("Test User");
      currentUser.setTlf("87654321");
      currentUser.setRole(Role.USER);
      currentUser.setHousehold(household);

      // Mock security context
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("test@example.com");
      SecurityContextHolder.setContext(securityContext);

      // Mock repository responses
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
      List<User> users = Collections.singletonList(currentUser);
      when(userRepository.getUsersByHousehold(household)).thenReturn(users);
      List<UnregisteredHouseholdMember> unregisteredMembers = Collections.emptyList();
      when(unregisteredHouseholdMemberRepository.findUnregisteredHouseholdMembersByHousehold(
          household))
          .thenReturn(unregisteredMembers);

      // Execute
      Map<String, Object> result = householdService.getHouseholdDetails();

      // Verify
      assertNotNull(result);

      @SuppressWarnings("unchecked")
      List<UserResponseDto> usersList = (List<UserResponseDto>) result.get("users");
      assertEquals(1, usersList.size());

      @SuppressWarnings("unchecked")
      List<UnregisteredMemberResponseDto> unregisteredList =
          (List<UnregisteredMemberResponseDto>) result.get("unregisteredMembers");
      assertEquals(0, unregisteredList.size());
    }
  }
}

