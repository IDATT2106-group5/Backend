package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import edu.ntnu.idatt2106.krisefikser.api.dto.household.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.EditMemberDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.UnregisteredHouseholdMember;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UnregisteredHouseholdMemberRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Unit tests for the HouseholdService class.
 * This class contains nested test classes for testing various functionalities of the HouseholdService.
 */
class HouseholdServiceTest {

  @Mock
  private HouseholdRepository householdRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UnregisteredHouseholdMemberRepository unregisteredHouseholdMemberRepository;

  private HouseholdService householdService;

  /**
   * Initializes mocks and the HouseholdService instance before each test.
   */
  @BeforeEach
  void setUp() {
    openMocks(this);
    householdService = new HouseholdService(householdRepository, userRepository,
        unregisteredHouseholdMemberRepository);
  }

  /**
   * Nested test class for testing the createHousehold functionality.
   */
  @Nested
  class createHouseholdTests {
    /**
     * Tests the positive scenario where a household is successfully created.
     */
    @Test
    void createHouseholdPositive() {
      CreateHouseholdRequestDto request = mock(CreateHouseholdRequestDto.class);
      when(request.getName()).thenReturn("TestHousehold");
      when(request.getAddress()).thenReturn("TestAddress");
      when(request.getOwnerId()).thenReturn(1L);

      when(householdRepository.findByName("TestHousehold")).thenReturn(Optional.empty());
      when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

      // mock save operation so that household ID is set
      when(householdRepository.save(any(Household.class))).thenAnswer(invocation -> {
        Household household = invocation.getArgument(0);
        household.setId(1L);
        return household;
      });

      assertDoesNotThrow(() -> householdService.createHousehold(request));

      verify(householdRepository, times(1)).save(any(Household.class));
      verify(userRepository, times(1)).updateHouseholdId(eq(1L), anyLong());
    }

    /**
     * Tests the scenario where a household with a duplicate name is attempted to be created.
     */
    @Test
    void createHouseholdWithDuplicateName() {
      CreateHouseholdRequestDto request = mock(CreateHouseholdRequestDto.class);
      when(request.getName()).thenReturn("TestHousehold");

      when(householdRepository.findByName("TestHousehold")).thenReturn(
          Optional.of(new Household()));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.createHousehold(request));
      assertEquals("Household with this name already exists", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }

    /**
     * Tests the scenario where the owner ID is null during household creation.
     */
    @Test
    void createHouseholdWithNullOwnerId() {
      CreateHouseholdRequestDto request = mock(CreateHouseholdRequestDto.class);
      when(request.getName()).thenReturn("TestHousehold");
      when(request.getAddress()).thenReturn("TestAddress");
      when(request.getOwnerId()).thenReturn(null);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.createHousehold(request));
      assertEquals("Owner id must not be null", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }

    /**
     * Tests the scenario where the owner does not exist in the database.
     */
    @Test
    void createHouseholdWithNonExistentOwner() {
      CreateHouseholdRequestDto request = mock(CreateHouseholdRequestDto.class);
      when(request.getName()).thenReturn("TestHousehold");
      when(request.getAddress()).thenReturn("TestAddress");
      when(request.getOwnerId()).thenReturn(1L);

      when(householdRepository.findByName("TestHousehold")).thenReturn(Optional.empty());
      when(userRepository.findById(1L)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.createHousehold(request));
      assertEquals("User not found", exception.getMessage());

      verify(householdRepository, never()).save(any(Household.class));
    }
  }

  /**
   * Nested test class for testing the addUserToHousehold functionality.
   */
  @Nested
  class addUserToHouseholdTest {
    /**
     * Tests the positive scenario where a user is successfully added to a household.
     */
    @Test
    void addUserToHousehold() {
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
      when(request.getEmail()).thenReturn("user@example.com");
      when(request.getHouseholdId()).thenReturn(2L);

      User user = new User();
      user.setId(1L);
      user.setHousehold(null);
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

      Household household = new Household();
      household.setId(2L);
      household.setNumberOfMembers(3);
      when(householdRepository.findById(2L)).thenReturn(Optional.of(household));

      assertDoesNotThrow(() -> householdService.addUserToHousehold(request));

      verify(userRepository, times(1)).updateHouseholdId(eq(1L), eq(2L));
      verify(householdRepository, times(1)).updateNumberOfMembers(eq(2L), eq(4));
    }

    /**
     * Add user to household throws when user not found.
     */
    @Test
    void addUserToHouseholdThrowsWhenUserNotFound() {
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
      when(request.getEmail()).thenReturn("nonexistent@example.com");

      when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUserToHousehold(request));
      assertEquals("User not found", exception.getMessage());

      verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    /**
     * Tests the scenario where the user does not belong to any household yet.
     */
    @Test
    void addUserToHouseholdWhenUserDoesNotBelongToAnyHousehold() {
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
      when(request.getEmail()).thenReturn("new@example.com");
      when(request.getHouseholdId()).thenReturn(1L);

      User user = new User();
      user.setId(3L);
      user.setHousehold(null); // User doesn't belong to any household
      when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(user));

      Household household = new Household();
      household.setId(1L);
      household.setNumberOfMembers(2);
      when(householdRepository.findById(1L)).thenReturn(Optional.of(household));

      assertDoesNotThrow(() -> householdService.addUserToHousehold(request));

      verify(userRepository, times(1)).updateHouseholdId(eq(3L), eq(1L));
      verify(householdRepository, times(1)).updateNumberOfMembers(eq(1L), eq(3));
      verify(householdRepository, never()).updateNumberOfMembers(anyLong(),
          eq(household.getNumberOfMembers() - 1));
    }

    /**
     * Tests the scenario where the household is not found.
     */
    @Test
    void addUserToHouseholdThrowsWhenHouseholdNotFound() {
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
      when(request.getEmail()).thenReturn("user@example.com");
      when(request.getHouseholdId()).thenReturn(3L);

      User user = new User();
      user.setId(1L);
      user.setHousehold(null);
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

      when(householdRepository.findById(3L)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUserToHousehold(request));
      assertEquals("Household not found", exception.getMessage());

      verify(householdRepository, times(1)).findById(3L);
    }

    /**
     * Tests the scenario where the user is already a member of the target household.
     */
    @Test
    void addUserToHouseholdThrowsWhenUserAlreadyMemberOfTargetHousehold() {
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
      when(request.getEmail()).thenReturn("user@example.com");
      when(request.getHouseholdId()).thenReturn(2L);

      Household currentHousehold = new Household();
      currentHousehold.setId(2L);
      currentHousehold.setNumberOfMembers(4);
      User user = new User();
      user.setId(1L);
      user.setHousehold(currentHousehold);
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
      when(householdRepository.findById(2L)).thenReturn(Optional.of(currentHousehold));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUserToHousehold(request));
      assertEquals("User is already a member of this household", exception.getMessage());

      verify(householdRepository, never()).updateNumberOfMembers(anyLong(), anyInt());
      verify(userRepository, never()).updateHouseholdId(anyLong(), anyLong());
    }

    /**
     * Tests the scenario where the user is moved from one household to another.
     */
    @Test
    void addUserToHouseholdMovesUserFromOldHousehold() {
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
      when(request.getEmail()).thenReturn("user@example.com");
      when(request.getHouseholdId()).thenReturn(5L);

      Household oldHousehold = new Household();
      oldHousehold.setId(3L);
      oldHousehold.setNumberOfMembers(4);
      User user = new User();
      user.setId(1L);
      user.setHousehold(oldHousehold);
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

      Household newHousehold = new Household();
      newHousehold.setId(5L);
      newHousehold.setNumberOfMembers(2);
      when(householdRepository.findById(5L)).thenReturn(Optional.of(newHousehold));

      assertDoesNotThrow(() -> householdService.addUserToHousehold(request));

      verify(householdRepository, times(1))
          .updateNumberOfMembers(eq(oldHousehold.getId()),
              eq(oldHousehold.getNumberOfMembers() - 1));
      verify(userRepository, times(1)).updateHouseholdId(eq(1L), eq(newHousehold.getId()));
      verify(householdRepository, times(1))
          .updateNumberOfMembers(eq(newHousehold.getId()),
              eq(newHousehold.getNumberOfMembers() + 1));
    }

  }

  /**
   * Nested test class for testing the removeUserFromHousehold functionality.
   */
  @Nested
  class RemoveUserFromHouseholdTests {

    /**
     * Tests the positive scenario where a user is successfully removed from a household.
     */
    @Test
    void removeUserFromHouseholdPositive() {
      User user = new User();
      user.setId(1L);
      Household household = new Household();
      household.setId(2L);
      household.setNumberOfMembers(3);
      user.setHousehold(household);

      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

      assertDoesNotThrow(() -> householdService.removeUserFromHousehold("user@example.com"));

      verify(householdRepository, times(1)).updateNumberOfMembers(eq(2L), eq(2));
      verify(userRepository, times(1)).updateHouseholdId(eq(1L), eq(null));
    }

    /**
     * Tests the scenario where the user is not found in the database, and an exception is thrown.
     */
    @Test
    void removeUserFromHouseholdThrowsWhenUserNotFound() {
      when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.removeUserFromHousehold("nonexistent@example.com"));
      assertEquals("User not found", exception.getMessage());

      verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    /**
     * Tests the scenario where the user is not a member of any household, and an exception is thrown.
     */
    @Test
    void removeUserFromHouseholdThrowsWhenUserNotInAnyHousehold() {
      User user = new User();
      user.setId(1L);
      user.setHousehold(null);

      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.removeUserFromHousehold("user@example.com"));
      assertEquals("User is not a member of any household", exception.getMessage());

      verify(householdRepository, never()).updateNumberOfMembers(anyLong(), anyInt());
      verify(userRepository, never()).updateHouseholdId(anyLong(), any());
    }
  }

  /**
   * Nested test class for testing the addUnregisteredMemberToHousehold functionality.
   */
  @Nested
  class AddUnregisteredMemberToHouseholdTests {

    /**
     * Tests the positive scenario where an unregistered member is successfully added to a household.
     */
    @Test
    void addUnregisteredMemberToHouseholdPositive() {
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          mock(UnregisteredMemberHouseholdAssignmentRequestDto.class);
      when(request.getFullName()).thenReturn("John Doe");
      when(request.getHouseholdId()).thenReturn(1L);

      Household household = new Household();
      household.setId(1L);
      household.setNumberOfMembers(2);

      when(householdRepository.findById(1L)).thenReturn(Optional.of(household));
      when(unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId("John Doe", 1L))
          .thenReturn(Optional.empty());

      assertDoesNotThrow(() -> householdService.addUnregisteredMemberToHousehold(request));

      verify(unregisteredHouseholdMemberRepository, times(1)).save(
          any(UnregisteredHouseholdMember.class));
      verify(householdRepository, times(1)).updateNumberOfMembers(eq(1L), eq(3));
    }

    /**
     * Tests the scenario where an unregistered member already exists in the household, and an exception is thrown.
     */
    @Test
    void addUnregisteredMemberToHouseholdThrowsWhenMemberAlreadyExists() {
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          mock(UnregisteredMemberHouseholdAssignmentRequestDto.class);
      when(request.getFullName()).thenReturn("John Doe");
      when(request.getHouseholdId()).thenReturn(1L);

      when(unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId("John Doe", 1L))
          .thenReturn(Optional.of(new UnregisteredHouseholdMember()));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUnregisteredMemberToHousehold(request));
      assertEquals("Unregistered member already exists in this household", exception.getMessage());

      verify(unregisteredHouseholdMemberRepository, never()).save(
          any(UnregisteredHouseholdMember.class));
      verify(householdRepository, never()).updateNumberOfMembers(anyLong(), anyInt());
    }

    /**
     * Tests the scenario where the household is not found, and an exception is thrown.
     */
    @Test
    void addUnregisteredMemberToHouseholdThrowsWhenHouseholdNotFound() {
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          mock(UnregisteredMemberHouseholdAssignmentRequestDto.class);
      when(request.getFullName()).thenReturn("John Doe");
      when(request.getHouseholdId()).thenReturn(1L);

      when(householdRepository.findById(1L)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.addUnregisteredMemberToHousehold(request));
      assertEquals("Household not found", exception.getMessage());

      verify(unregisteredHouseholdMemberRepository, never()).save(
          any(UnregisteredHouseholdMember.class));
      verify(householdRepository, never()).updateNumberOfMembers(anyLong(), anyInt());
    }
  }

  /**
   * Nested test class for testing the removeUnregisteredMemberFromHousehold functionality
   * that takes a memberId parameter.
   */
  @Nested
  class RemoveUnregisteredMemberFromHouseholdTests {

    @Test
    void removeUnregisteredMemberFromHouseholdSuccessfullyRemovesMember() {
      Long memberId = 1L;
      Household household = new Household();
      household.setId(2L);
      household.setNumberOfMembers(3);

      UnregisteredHouseholdMember member = new UnregisteredHouseholdMember();
      member.setId(memberId);
      member.setHousehold(household);

      when(unregisteredHouseholdMemberRepository.findById(memberId)).thenReturn(
          Optional.of(member));

      assertDoesNotThrow(() -> householdService.removeUnregisteredMemberFromHousehold(memberId));

      verify(unregisteredHouseholdMemberRepository, times(1)).delete(member);
      verify(householdRepository, times(1)).updateNumberOfMembers(eq(2L), eq(2));
    }

    @Test
    void removeUnregisteredMemberFromHouseholdThrowsWhenMemberNotFound() {
      Long memberId = 1L;

      when(unregisteredHouseholdMemberRepository.findById(memberId)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.removeUnregisteredMemberFromHousehold(memberId));

      assertEquals("Unregistered member not found", exception.getMessage());
      verify(unregisteredHouseholdMemberRepository, never()).delete(
          any(UnregisteredHouseholdMember.class));
      verify(householdRepository, never()).updateNumberOfMembers(anyLong(), anyInt());
    }

    @Test
    void removeUnregisteredMemberFromHouseholdLogsWarningWhenMemberHasNoHousehold() {
      Long memberId = 1L;
      UnregisteredHouseholdMember member = new UnregisteredHouseholdMember();
      member.setId(memberId);
      member.setHousehold(null);

      when(unregisteredHouseholdMemberRepository.findById(memberId)).thenReturn(
          Optional.of(member));

      assertDoesNotThrow(() -> householdService.removeUnregisteredMemberFromHousehold(memberId));

      verify(unregisteredHouseholdMemberRepository, times(1)).delete(member);
      verify(householdRepository, never()).updateNumberOfMembers(anyLong(), anyInt());
    }
  }

  @Nested
  class GetHouseholdDetailsTests {

    @Test
    void getHouseholdDetailsReturnsCorrectDetailsWhenHouseholdExists() {
      Long householdId = 1L;

      Household household = new Household();
      household.setId(householdId);
      household.setName("Test Household");
      household.setAddress("Test Address");
      User owner = new User();
      owner.setId(1L);
      owner.setEmail("owner@example.com");
      owner.setFullName("Owner Name");
      owner.setRole(Role.USER);
      household.setOwner(owner);

      User user = new User();
      user.setId(2L);
      user.setEmail("user@example.com");
      user.setFullName("User Name");
      user.setRole(Role.USER);

      UnregisteredHouseholdMember unregisteredMember = new UnregisteredHouseholdMember();
      unregisteredMember.setId(3L);
      unregisteredMember.setFullName("Unregistered Member");

      when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));
      when(userRepository.getUsersByHousehold(household)).thenReturn(List.of(user));
      when(unregisteredHouseholdMemberRepository.findUnregisteredHouseholdMembersByHousehold(
          household))
          .thenReturn(List.of(unregisteredMember));

      Map<String, Object> result = householdService.getHouseholdDetails(householdId);

      assertNotNull(result);
      assertEquals("Test Household", ((HouseholdResponseDto) result.get("household")).getName());
      assertEquals(1, ((List<?>) result.get("users")).size());
      assertEquals(1, ((List<?>) result.get("unregisteredMembers")).size());
    }

    @Test
    void getHouseholdDetailsThrowsWhenHouseholdNotFound() {
      Long householdId = 1L;

      when(householdRepository.findById(householdId)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.getHouseholdDetails(householdId));

      assertEquals("Household not found", exception.getMessage());
      verify(householdRepository, times(1)).findById(householdId);
    }

    @Test
    void getHouseholdDetailsReturnsEmptyListsWhenNoUsersOrUnregisteredMembers() {
      Long householdId = 1L;

      Household household = new Household();
      household.setId(householdId);
      household.setName("Test Household");
      household.setAddress("Test Address");
      User owner = new User();
      owner.setId(1L);
      owner.setEmail("owner@example.com");
      owner.setFullName("Owner Name");
      owner.setRole(Role.USER);
      household.setOwner(owner);

      when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));
      when(userRepository.getUsersByHousehold(household)).thenReturn(List.of());
      when(unregisteredHouseholdMemberRepository.findUnregisteredHouseholdMembersByHousehold(
          household))
          .thenReturn(List.of());

      Map<String, Object> result = householdService.getHouseholdDetails(householdId);

      assertNotNull(result);
      assertEquals("Test Household", ((HouseholdResponseDto) result.get("household")).getName());
      assertTrue(((List<?>) result.get("users")).isEmpty());
      assertTrue(((List<?>) result.get("unregisteredMembers")).isEmpty());
    }
  }

  /**
   * Nested test class for testing the editUnregisteredMemberInHousehold functionality.
   */
  @Nested
  class EditUnregisteredMemberInHouseholdTests {

    /**
     * Tests the positive scenario where an unregistered member is successfully edited.
     */
    @Test
    void editUnregisteredMemberInHouseholdPositive() {
      EditMemberDto request = mock(EditMemberDto.class);
      when(request.getNewFullName()).thenReturn("New Name");
      when(request.getHouseholdId()).thenReturn(1L);

      UnregisteredHouseholdMember member = new UnregisteredHouseholdMember();
      member.setFullName("Old Name");

      when(unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId("Old Name", 1L))
          .thenReturn(Optional.of(member));

      assertDoesNotThrow(() -> householdService.editUnregisteredMemberInHousehold(request));

      assertEquals("New Name", member.getFullName());
      verify(unregisteredHouseholdMemberRepository, times(1)).save(member);
    }

    /**
     * Tests the scenario where the unregistered member is not found.
     */
    @Test
    void editUnregisteredMemberInHouseholdThrowsWhenMemberNotFound() {
      EditMemberDto request = mock(EditMemberDto.class);
      when(request.getHouseholdId()).thenReturn(1L);

      when(unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId("Nonexistent Member",
          1L))
          .thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> householdService.editUnregisteredMemberInHousehold(request));
      assertEquals("Unregistered member not found in household", exception.getMessage());
    }

    /**
     * Tests the scenario where the new full name is null.
     */
    @Test
    void editUnregisteredMemberInHouseholdKeepsOriginalNameWhenNewNameIsNull() {
      EditMemberDto request = mock(EditMemberDto.class);
      when(request.getNewFullName()).thenReturn(null);
      when(request.getHouseholdId()).thenReturn(1L);

      UnregisteredHouseholdMember member = new UnregisteredHouseholdMember();
      member.setFullName("Original Name");

      when(unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId("Original Name", 1L))
          .thenReturn(Optional.of(member));

      assertDoesNotThrow(() -> householdService.editUnregisteredMemberInHousehold(request));

      assertEquals("Original Name", member.getFullName());
      verify(unregisteredHouseholdMemberRepository, times(1)).save(member);
    }
  }
}