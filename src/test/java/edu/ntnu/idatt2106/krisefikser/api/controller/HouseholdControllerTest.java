package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.controller.household.HouseholdController;
import edu.ntnu.idatt2106.krisefikser.api.dto.PositionResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.EditHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdBasicResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.EditMemberDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.RemoveUnregisteredMemberRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.service.HouseholdService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for the HouseholdController class. This class contains nested test classes for testing
 * various endpoints of the HouseholdController.
 */
class HouseholdControllerTest {

  @Mock
  private HouseholdService householdService;

  @InjectMocks
  private HouseholdController householdController;

  /**
   * Initializes mocks and the HouseholdController instance before each test.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testLeaveHouseholdValidationError() {
    // Given
    doThrow(new IllegalArgumentException("You are not a member of any household"))
        .when(householdService).leaveCurrentUserFromHousehold();

    // When
    ResponseEntity<String> response = householdController.leaveHousehold();

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("You are not a member of any household", response.getBody());
  }

  @Test
  void testLeaveHouseholdInternalError() {
    // Given
    doThrow(new RuntimeException("Database error"))
        .when(householdService).leaveCurrentUserFromHousehold();

    // When
    ResponseEntity<String> response = householdController.leaveHousehold();

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Uventet feil under utmelding fra husstand.", response.getBody());
  }

  @Test
  void testDeleteHouseholdSuccess() {
    // Given
    doNothing().when(householdService).deleteHousehold();

    // When
    ResponseEntity<String> response = householdController.deleteHousehold();

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Household deleted successfully", response.getBody());
    verify(householdService).deleteHousehold();
  }

  @Test
  void testDeleteHouseholdValidationError() {
    // Given
    doThrow(new IllegalArgumentException("Only the owner can delete the household"))
        .when(householdService).deleteHousehold();

    // When
    ResponseEntity<String> response = householdController.deleteHousehold();

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Only the owner can delete the household", response.getBody());
  }

  @Test
  void testDeleteHouseholdInternalError() {
    // Given
    doThrow(new RuntimeException("Database error"))
        .when(householdService).deleteHousehold();

    // When
    ResponseEntity<String> response = householdController.deleteHousehold();

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody());
  }

  @Test
  void testEditHouseholdSuccess() {
    // Given
    EditHouseholdRequestDto requestDto = new EditHouseholdRequestDto();
    requestDto.setName("New Name");
    requestDto.setAddress("New Address");
    doNothing().when(householdService).editHousehold(requestDto);

    // When
    ResponseEntity<String> response = householdController.editHousehold(requestDto);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Household edited successfully", response.getBody());
    verify(householdService).editHousehold(requestDto);
  }

  @Test
  void testEditHouseholdValidationError() {
    // Given
    EditHouseholdRequestDto requestDto = new EditHouseholdRequestDto();
    requestDto.setName("New Name");
    doThrow(new IllegalArgumentException("Only the owner can edit the household"))
        .when(householdService).editHousehold(requestDto);

    // When
    ResponseEntity<String> response = householdController.editHousehold(requestDto);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Only the owner can edit the household", response.getBody());
  }

  @Test
  void testEditHouseholdInternalError() {
    // Given
    EditHouseholdRequestDto requestDto = new EditHouseholdRequestDto();
    requestDto.setName("New Name");
    doThrow(new RuntimeException("Database error"))
        .when(householdService).editHousehold(requestDto);

    // When
    ResponseEntity<String> response = householdController.editHousehold(requestDto);

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody());
  }

  @Test
  void testRemoveUserFromHouseholdSuccess() {
    // Given
    UserHouseholdAssignmentRequestDto requestDto = new UserHouseholdAssignmentRequestDto();
    requestDto.setUserId("user123");
    doNothing().when(householdService).removeUserFromHousehold("user123");

    // When
    ResponseEntity<String> response = householdController.removeUserFromHousehold(requestDto);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User removed from household successfully", response.getBody());
    verify(householdService).removeUserFromHousehold("user123");
  }

  @Test
  void testRemoveUserFromHouseholdValidationError() {
    // Given
    UserHouseholdAssignmentRequestDto requestDto = new UserHouseholdAssignmentRequestDto();
    requestDto.setUserId("user123");
    doThrow(new IllegalArgumentException("User not found"))
        .when(householdService).removeUserFromHousehold("user123");

    // When
    ResponseEntity<String> response = householdController.removeUserFromHousehold(requestDto);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User not found", response.getBody());
  }

  @Test
  void testRemoveUserFromHouseholdInternalError() {
    // Given
    UserHouseholdAssignmentRequestDto requestDto = new UserHouseholdAssignmentRequestDto();
    requestDto.setUserId("user123");
    doThrow(new RuntimeException("Database error"))
        .when(householdService).removeUserFromHousehold("user123");

    // When
    ResponseEntity<String> response = householdController.removeUserFromHousehold(requestDto);

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody());
  }

  @Test
  void testGetHouseholdPositionsSuccess() {
    // Given
    List<PositionResponseDto> positions = Arrays.asList(
        new PositionResponseDto("user123", "John Doe", "10.123", "60.123"),
        new PositionResponseDto("user456", "Jane Doe", "11.345", "61.345")
    );
    when(householdService.getHouseholdPositions()).thenReturn(positions);

    // When
    ResponseEntity<?> response = householdController.getHouseholdPositions();

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(positions, response.getBody());
    verify(householdService).getHouseholdPositions();
  }

  @Test
  void testGetHouseholdPositionsValidationError() {
    // Given
    doThrow(new IllegalArgumentException("User does not belong to a household"))
        .when(householdService).getHouseholdPositions();

    // When
    ResponseEntity<?> response = householdController.getHouseholdPositions();

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Map.of("error", "User does not belong to a household"), response.getBody());
  }

  @Test
  void testGetHouseholdPositionsInternalError() {
    // Given
    doThrow(new RuntimeException("Database error"))
        .when(householdService).getHouseholdPositions();

    // When
    ResponseEntity<?> response = householdController.getHouseholdPositions();

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(Map.of("error", "Internal server error"), response.getBody());
  }

  @Test
  void testChangeHouseholdOwnerSuccess() {
    // Given
    UserHouseholdAssignmentRequestDto requestDto = new UserHouseholdAssignmentRequestDto();
    requestDto.setUserId("user123");
    requestDto.setHouseholdId("house123");
    doNothing().when(householdService).changeHouseholdOwner(requestDto);

    // When
    ResponseEntity<?> response = householdController.changeHouseholdOwner(requestDto);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Household owner changed successfully", response.getBody());
    verify(householdService).changeHouseholdOwner(requestDto);
  }

  @Test
  void testChangeHouseholdOwnerValidationError() {
    // Given
    UserHouseholdAssignmentRequestDto requestDto = new UserHouseholdAssignmentRequestDto();
    requestDto.setUserId("user123");
    doThrow(new IllegalArgumentException("Only the owner can transfer ownership"))
        .when(householdService).changeHouseholdOwner(requestDto);

    // When
    ResponseEntity<?> response = householdController.changeHouseholdOwner(requestDto);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Map.of("error", "Only the owner can transfer ownership"), response.getBody());
  }

  @Test
  void testChangeHouseholdOwnerInternalError() {
    // Given
    UserHouseholdAssignmentRequestDto requestDto = new UserHouseholdAssignmentRequestDto();
    requestDto.setUserId("user123");
    doThrow(new RuntimeException("Database error"))
        .when(householdService).changeHouseholdOwner(requestDto);

    // When
    ResponseEntity<?> response = householdController.changeHouseholdOwner(requestDto);

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(Map.of("error", "Internal server error"), response.getBody());
  }

  @Test
  void testSearchHouseholdByIdSuccess() {
    // Given
    Map<String, String> request = Map.of("householdId", "house123");
    HouseholdBasicResponseDto responseDto = new HouseholdBasicResponseDto("house123",
        "Test Household");
    when(householdService.searchHouseholdById("house123")).thenReturn(responseDto);

    // When
    ResponseEntity<?> response = householdController.searchHouseholdById(request);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(responseDto, response.getBody());
    verify(householdService).searchHouseholdById("house123");
  }

  @Test
  void testSearchHouseholdByIdValidationError() {
    // Given
    Map<String, String> request = Map.of("householdId", "house123");
    doThrow(new IllegalArgumentException("Household not found"))
        .when(householdService).searchHouseholdById("house123");

    // When
    ResponseEntity<?> response = householdController.searchHouseholdById(request);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Map.of("error", "Household not found"), response.getBody());
  }

  @Test
  void testSearchHouseholdByIdInternalError() {
    // Given
    Map<String, String> request = Map.of("householdId", "house123");
    doThrow(new RuntimeException("Database error"))
        .when(householdService).searchHouseholdById("house123");

    // When
    ResponseEntity<?> response = householdController.searchHouseholdById(request);

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(Map.of("error", "Internal server error"), response.getBody());
  }

  @Test
  void testGetHouseholdDetailsSuccess() {
    // Given
    Map<String, Object> details = new HashMap<>();
    HouseholdResponseDto householdResponseDto = new HouseholdResponseDto(
        "house123",
        "Test Household",
        "123 Main St",
        new UserResponseDto("user123", "test@example.com", "John Doe", "12345678", Role.USER)
    );
    details.put("household", householdResponseDto);
    details.put("users", Arrays.asList(
        new UserResponseDto("user123", "test@example.com", "John Doe", "12345678", Role.USER),
        new UserResponseDto("user456", "test2@example.com", "Jane Doe", "87654321", Role.USER)
    ));
    details.put("unregisteredMembers", List.of(
        new UnregisteredMemberResponseDto(1L, "Baby Doe")
    ));

    when(householdService.getHouseholdDetails()).thenReturn(details);

    // When
    ResponseEntity<?> response = householdController.getHouseholdDetails();

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(details, response.getBody());
    verify(householdService).getHouseholdDetails();
  }

  @Test
  void testGetHouseholdDetailsUserHasNoHousehold() {
    // Given
    when(householdService.getHouseholdDetails()).thenReturn(new HashMap<>());

    // When
    ResponseEntity<?> response = householdController.getHouseholdDetails();

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(Map.of("error", "Brukeren tilhører ingen husstand"), response.getBody());
  }

  @Test
  void testGetHouseholdDetailsInternalError() {
    // Given
    doThrow(new RuntimeException("Database error"))
        .when(householdService).getHouseholdDetails();

    // When
    ResponseEntity<?> response = householdController.getHouseholdDetails();

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(Map.of("error", "Internal server error"), response.getBody());
  }

  @Test
  void testEditUnregisteredMemberInHouseholdSuccess() {
    // Given
    EditMemberDto requestDto = new EditMemberDto();
    requestDto.setMemberId(1L);
    requestDto.setNewFullName("Jane Doe");
    doNothing().when(householdService).editUnregisteredMemberInHousehold(requestDto);

    // When
    ResponseEntity<String> response = householdController.editUnregisteredMemberInHousehold(
        requestDto);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Unregistered member edited in household successfully", response.getBody());
    verify(householdService).editUnregisteredMemberInHousehold(requestDto);
  }

  @Test
  void testEditUnregisteredMemberInHouseholdValidationError() {
    // Given
    EditMemberDto requestDto = new EditMemberDto();
    requestDto.setMemberId(1L);
    requestDto.setNewFullName("Jane Doe");
    doThrow(new IllegalArgumentException("Unregistered member not found"))
        .when(householdService).editUnregisteredMemberInHousehold(requestDto);

    // When
    ResponseEntity<String> response = householdController.editUnregisteredMemberInHousehold(
        requestDto);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Unregistered member not found", response.getBody());
  }

  @Test
  void testEditUnregisteredMemberInHouseholdInternalError() {
    // Given
    EditMemberDto requestDto = new EditMemberDto();
    requestDto.setMemberId(1L);
    requestDto.setNewFullName("Jane Doe");
    doThrow(new RuntimeException("Database error"))
        .when(householdService).editUnregisteredMemberInHousehold(requestDto);

    // When
    ResponseEntity<String> response = householdController.editUnregisteredMemberInHousehold(
        requestDto);

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody());
  }

  @Test
  void removeUserFromHouseholdUnexpectedExceptionReturnsInternalServerError() {
    // Arrange
    UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
    when(request.getUserId()).thenReturn("user-123");
    doThrow(new RuntimeException("Database error"))
        .when(householdService).removeUserFromHousehold("user-123");

    // Act
    ResponseEntity<String> response = householdController.removeUserFromHousehold(request);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody());
    verify(householdService, times(1)).removeUserFromHousehold("user-123");
  }

  /**
   * Tests the positive scenario where an unregistered member is successfully removed from a
   * household.
   */
  @Test
  void removeUnregisteredMemberFromHouseholdValidMemberIdReturnsOk() {
    // Arrange
    RemoveUnregisteredMemberRequestDto request = mock(RemoveUnregisteredMemberRequestDto.class);
    when(request.getMemberId()).thenReturn(1L);
    doNothing().when(householdService).removeUnregisteredMemberFromHousehold(1L);

    // Act
    ResponseEntity<String> response = householdController.removeUnregisteredMemberFromHousehold(
        request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Unregistered member removed from household successfully", response.getBody());
    verify(householdService, times(1)).removeUnregisteredMemberFromHousehold(1L);
  }

  /**
   * Tests the scenario where validation fails during unregistered member removal from a household.
   */
  @Test
  void removeUnregisteredMemberFromHouseholdValidationExceptionReturnsBadRequest() {
    // Arrange
    RemoveUnregisteredMemberRequestDto request = mock(RemoveUnregisteredMemberRequestDto.class);
    when(request.getMemberId()).thenReturn(-1L);
    String errorMessage = "Invalid member ID";
    doThrow(new IllegalArgumentException(errorMessage))
        .when(householdService).removeUnregisteredMemberFromHousehold(-1L);

    // Act
    ResponseEntity<String> response = householdController.removeUnregisteredMemberFromHousehold(
        request);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(errorMessage, response.getBody());
    verify(householdService, times(1)).removeUnregisteredMemberFromHousehold(-1L);
  }

  /**
   * Tests the scenario where an unexpected exception occurs during unregistered member removal.
   */
  @Test
  void removeUnregisteredMemberFromHouseholdUnexpectedExceptionReturnsInternalServerError() {
    // Arrange
    RemoveUnregisteredMemberRequestDto request = mock(RemoveUnregisteredMemberRequestDto.class);
    when(request.getMemberId()).thenReturn(1L);
    doThrow(new RuntimeException("Database error"))
        .when(householdService).removeUnregisteredMemberFromHousehold(1L);

    // Act
    ResponseEntity<String> response = householdController.removeUnregisteredMemberFromHousehold(
        request);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody());
    verify(householdService, times(1)).removeUnregisteredMemberFromHousehold(1L);
  }

  /**
   * Tests the scenario where a null member ID is provided.
   */
  @Test
  void removeUnregisteredMemberFromHouseholdNullMemberIdReturnsInternalServerError() {
    // Arrange
    RemoveUnregisteredMemberRequestDto request = mock(RemoveUnregisteredMemberRequestDto.class);
    when(request.getMemberId()).thenReturn(null);
    doThrow(new RuntimeException("Database error"))
        .when(householdService).removeUnregisteredMemberFromHousehold(null);

    // Act
    ResponseEntity<String> response = householdController.removeUnregisteredMemberFromHousehold(
        request);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody());
    verify(householdService, times(1)).removeUnregisteredMemberFromHousehold(null);
  }

  /**
   * Tests the scenario where household details are successfully retrieved.
   */
  @Test
  void getHouseholdDetailsValidHouseholdIdReturnsDetails() {
    // Arrange
    HouseholdResponseDto dummyHousehold = mock(HouseholdResponseDto.class);
    Map<String, Object> details = Map.of("household", dummyHousehold);
    when(householdService.getHouseholdDetails()).thenReturn(details);

    // Act
    ResponseEntity<?> response = householdController.getHouseholdDetails();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(details, response.getBody());
    verify(householdService, times(1)).getHouseholdDetails();
  }

  /**
   * Tests the scenario where an invalid household ID leads to a bad request.
   */
  @Test
  void getHouseholdDetailsInvalidHouseholdIdReturnsBadRequest() {
    // Arrange
    String errorMessage = "Invalid household ID";
    when(householdService.getHouseholdDetails())
        .thenThrow(new IllegalArgumentException(errorMessage));

    // Act
    ResponseEntity<?> response = householdController.getHouseholdDetails();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Map.of("error", errorMessage), response.getBody());
    verify(householdService, times(1)).getHouseholdDetails();
  }

  /**
   * Tests the scenario where an unexpected exception occurs during household details retrieval.
   */
  @Test
  void getHouseholdDetailsServiceThrowsUnexpectedExceptionReturnsInternalServerError() {
    // Arrange
    when(householdService.getHouseholdDetails())
        .thenThrow(new RuntimeException("Unexpected error"));

    // Act
    ResponseEntity<?> response = householdController.getHouseholdDetails();

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(Map.of("error", "Internal server error"), response.getBody());
    verify(householdService, times(1)).getHouseholdDetails();
  }

  /**
   * Nested test class for testing the createHousehold endpoint.
   */
  @Nested
  class CreateHouseholdTests {

    /**
     * Tests the positive scenario where a household is successfully created.
     */
    @Test
    void createHouseholdValidRequestReturnsOk() {
      // Arrange
      CreateHouseholdRequestDto request = mock(CreateHouseholdRequestDto.class);
      when(request.getName()).thenReturn("TestHousehold");
      when(request.getAddress()).thenReturn("TestAddress");

      doNothing().when(householdService).createHousehold(any(CreateHouseholdRequestDto.class));

      // Act
      ResponseEntity<String> response = householdController.createHousehold(request);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Household created successfully", response.getBody());
      verify(householdService, times(1)).createHousehold(request);
    }

    /**
     * Tests the scenario where validation fails during household creation.
     */
    @Test
    void createHouseholdValidationExceptionReturnsBadRequest() {
      // Arrange
      CreateHouseholdRequestDto request = mock(CreateHouseholdRequestDto.class);
      when(request.getName()).thenReturn("TestHousehold");

      String errorMessage = "Owner id must not be null";
      doThrow(new IllegalArgumentException(errorMessage))
          .when(householdService).createHousehold(any(CreateHouseholdRequestDto.class));

      // Act
      ResponseEntity<String> response = householdController.createHousehold(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(errorMessage, response.getBody());
      verify(householdService, times(1)).createHousehold(request);
    }

    /**
     * Tests the scenario where an unexpected exception occurs during household creation.
     */
    @Test
    void createHouseholdUnexpectedExceptionReturnsInternalServerError() {
      // Arrange
      CreateHouseholdRequestDto request = mock(CreateHouseholdRequestDto.class);
      when(request.getName()).thenReturn("TestHousehold");

      doThrow(new RuntimeException("Database error"))
          .when(householdService).createHousehold(any(CreateHouseholdRequestDto.class));

      // Act
      ResponseEntity<String> response = householdController.createHousehold(request);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody());
      verify(householdService, times(1)).createHousehold(request);
    }
  }

  /**
   * Nested test class for testing the addUserToHousehold endpoint.
   */
  @Nested
  class AddUserToHouseholdTests {

    /**
     * Tests the positive scenario where a user is successfully added to a household.
     */
    @Test
    void addUserToHouseholdValidRequestReturnsOk() {
      // Arrange
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
      when(request.getHouseholdId()).thenReturn("1L");

      doNothing().when(householdService)
          .addUserToHousehold(any(UserHouseholdAssignmentRequestDto.class));

      // Act
      ResponseEntity<String> response = householdController.addUserToHousehold(request);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("User added to household successfully", response.getBody());
      verify(householdService, times(1)).addUserToHousehold(request);
    }

    /**
     * Tests the scenario where validation fails during user addition to a household.
     */
    @Test
    void addUserToHouseholdValidationExceptionReturnsBadRequest() {
      // Arrange
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);

      String errorMessage = "Household ID is required";
      doThrow(new IllegalArgumentException(errorMessage))
          .when(householdService).addUserToHousehold(any(UserHouseholdAssignmentRequestDto.class));

      // Act
      ResponseEntity<String> response = householdController.addUserToHousehold(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(errorMessage, response.getBody());
      verify(householdService, times(1)).addUserToHousehold(request);
    }

    /**
     * Tests the scenario where an unexpected exception occurs during user addition to a household.
     */
    @Test
    void addUserToHouseholdUnexpectedExceptionReturnsInternalServerError() {
      // Arrange
      UserHouseholdAssignmentRequestDto request = mock(UserHouseholdAssignmentRequestDto.class);
      when(request.getHouseholdId()).thenReturn("1L");

      doThrow(new RuntimeException("Database error"))
          .when(householdService).addUserToHousehold(any(UserHouseholdAssignmentRequestDto.class));

      // Act
      ResponseEntity<String> response = householdController.addUserToHousehold(request);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody());
      verify(householdService, times(1)).addUserToHousehold(request);
    }
  }

  /**
   * Nested test class for testing the addUnregisteredMemberToHousehold endpoint.
   */
  @Nested
  class AddUnregisteredMemberToHouseholdTests {

    /**
     * Tests the positive scenario where an unregistered member is successfully added to a
     * household.
     */
    @Test
    void addUnregisteredMemberToHouseholdValidRequestReturnsOk() {
      // Arrange
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          mock(UnregisteredMemberHouseholdAssignmentRequestDto.class);
      when(request.getFullName()).thenReturn("John Doe");

      doNothing().when(householdService).addUnregisteredMemberToHousehold(
          any(UnregisteredMemberHouseholdAssignmentRequestDto.class));

      // Act
      ResponseEntity<String> response =
          householdController.addUnregisteredMemberToHousehold(request);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Unregistered member added to household successfully", response.getBody());
      verify(householdService, times(1)).addUnregisteredMemberToHousehold(request);
    }

    /**
     * Tests the scenario where validation fails during unregistered member addition to a
     * household.
     */
    @Test
    void addUnregisteredMemberToHouseholdValidationExceptionReturnsBadRequest() {
      // Arrange
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          mock(UnregisteredMemberHouseholdAssignmentRequestDto.class);
      when(request.getFullName()).thenReturn("John Doe");

      String errorMessage = "Household ID is required";
      doThrow(new IllegalArgumentException(errorMessage))
          .when(householdService).addUnregisteredMemberToHousehold(
              any(UnregisteredMemberHouseholdAssignmentRequestDto.class));

      // Act
      ResponseEntity<String> response =
          householdController.addUnregisteredMemberToHousehold(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(errorMessage, response.getBody());
      verify(householdService, times(1)).addUnregisteredMemberToHousehold(request);
    }

    /**
     * Tests the scenario where an unexpected exception occurs during unregistered member addition.
     */
    @Test
    void addUnregisteredMemberToHouseholdUnexpectedExceptionReturnsInternalServerError() {
      // Arrange
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          mock(UnregisteredMemberHouseholdAssignmentRequestDto.class);
      when(request.getFullName()).thenReturn("John Doe");

      doThrow(new RuntimeException("Database error"))
          .when(householdService).addUnregisteredMemberToHousehold(
              any(UnregisteredMemberHouseholdAssignmentRequestDto.class));

      // Act
      ResponseEntity<String> response =
          householdController.addUnregisteredMemberToHousehold(request);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody());
      verify(householdService, times(1)).addUnregisteredMemberToHousehold(request);
    }
  }

  /**
   * Nested test class for testing the editUnregisteredMemberInHousehold endpoint.
   */
  @Nested
  class EditUnregisteredMemberInHouseholdTests {

    /**
     * Tests the positive scenario where an unregistered member is successfully edited in a
     * household.
     */
    @Test
    void editUnregisteredMemberInHouseholdValidRequestReturnsOk() {
      // Arrange
      EditMemberDto request = mock(EditMemberDto.class);
      when(request.getNewFullName()).thenReturn("John Doe");

      doNothing().when(householdService)
          .editUnregisteredMemberInHousehold(any(EditMemberDto.class));

      // Act
      ResponseEntity<String> response =
          householdController.editUnregisteredMemberInHousehold(request);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Unregistered member edited in household successfully", response.getBody());
      verify(householdService, times(1)).editUnregisteredMemberInHousehold(request);
    }

    /**
     * Tests the scenario where validation fails during unregistered member edit.
     */
    @Test
    void editUnregisteredMemberInHouseholdValidationExceptionReturnsBadRequest() {
      // Arrange
      EditMemberDto request = mock(EditMemberDto.class);
      when(request.getNewFullName()).thenReturn("John Doe");

      String errorMessage = "Household ID is required";
      doThrow(new IllegalArgumentException(errorMessage))
          .when(householdService).editUnregisteredMemberInHousehold(any(EditMemberDto.class));

      // Act
      ResponseEntity<String> response =
          householdController.editUnregisteredMemberInHousehold(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(errorMessage, response.getBody());
      verify(householdService, times(1)).editUnregisteredMemberInHousehold(request);
    }

    /**
     * Tests the scenario where an unexpected exception occurs during unregistered member edit.
     */
    @Test
    void editUnregisteredMemberInHouseholdUnexpectedExceptionReturnsInternalServerError() {
      // Arrange
      EditMemberDto request = mock(EditMemberDto.class);
      when(request.getNewFullName()).thenReturn("John Doe");

      doThrow(new RuntimeException("Database error"))
          .when(householdService).editUnregisteredMemberInHousehold(any(EditMemberDto.class));

      // Act
      ResponseEntity<String> response =
          householdController.editUnregisteredMemberInHousehold(request);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody());
      verify(householdService, times(1)).editUnregisteredMemberInHousehold(request);
    }
  }
}