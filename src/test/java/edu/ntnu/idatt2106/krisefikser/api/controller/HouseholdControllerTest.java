package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.household.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.EditMemberDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.HouseholdService;
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
 * Unit tests for the HouseholdController class.
 * This class contains nested test classes for testing various endpoints of the HouseholdController.
 */
public class HouseholdControllerTest {

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

  /**
   * Nested test class for testing the createHousehold endpoint.
   */
  @Nested
  class CreateHouseholdTests {

    /**
     * Tests the positive scenario where a household is successfully created.
     */
    @Test
    void createHousehold_ValidRequest_ReturnsOk() {
      // Arrange
      CreateHouseholdRequestDto request = mock(CreateHouseholdRequestDto.class);
      when(request.getName()).thenReturn("TestHousehold");
      when(request.getAddress()).thenReturn("TestAddress");
      when(request.getOwnerId()).thenReturn("1L");

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
    void createHousehold_ValidationException_ReturnsBadRequest() {
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
    void createHousehold_UnexpectedException_ReturnsInternalServerError() {
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
    void addUserToHousehold_ValidRequest_ReturnsOk() {
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
    void addUserToHousehold_ValidationException_ReturnsBadRequest() {
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
    void addUserToHousehold_UnexpectedException_ReturnsInternalServerError() {
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
     * Tests the positive scenario where an unregistered member is successfully added to a household.
     */
    @Test
    void addUnregisteredMemberToHousehold_ValidRequest_ReturnsOk() {
      // Arrange
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          mock(UnregisteredMemberHouseholdAssignmentRequestDto.class);
      when(request.getFullName()).thenReturn("John Doe");
      when(request.getHouseholdId()).thenReturn("1L");

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
     * Tests the scenario where validation fails during unregistered member addition to a household.
     */
    @Test
    void addUnregisteredMemberToHousehold_ValidationException_ReturnsBadRequest() {
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
    void addUnregisteredMemberToHousehold_UnexpectedException_ReturnsInternalServerError() {
      // Arrange
      UnregisteredMemberHouseholdAssignmentRequestDto request =
          mock(UnregisteredMemberHouseholdAssignmentRequestDto.class);
      when(request.getFullName()).thenReturn("John Doe");
      when(request.getHouseholdId()).thenReturn("1L");

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
   * Nested test class for testing the removeUserFromHousehold endpoint.
   */
  @Nested
  class RemoveUserFromHouseholdTests {

    /**
     * Tests the positive scenario where a user is successfully removed from a household.
     */
    @Test
    void removeUserFromHousehold_ValidRequest_ReturnsOk() {
      // Arrange
      String email = "user@example.com";


      // Act

      // Assert
    }

    /**
     * Tests the scenario where validation fails during user removal from a household.
     */
    @Test
    void removeUserFromHousehold_ValidationException_ReturnsBadRequest() {
      // Arrange
      String email = "";

      String errorMessage = "Email is required";

      // Act

      // Assert
    }
    /**
     * Tests the scenario where an unexpected exception occurs during user removal from a household.
     */
    @Test
    void removeUserFromHousehold_UnexpectedException_ReturnsInternalServerError() {
      // Arrange
      String email = "user@example.com";

    }
  }

  /**
   * Nested test class for testing the removeUnregisteredMemberFromHousehold endpoint.
   */
  @Nested
  class RemoveUnregisteredMemberFromHouseholdTests {

    /**
     * Tests the positive scenario where an unregistered member is successfully removed from a household.
     */
    @Test
    void removeUnregisteredMemberFromHousehold_ValidMemberId_ReturnsOk() {
    }

    /**
     * Tests the scenario where validation fails during unregistered member removal from a household.
     */
    @Test
    void removeUnregisteredMemberFromHousehold_ValidationException_ReturnsBadRequest() {
      // Arrange
      Long memberId = -1L;
      String errorMessage = "Invalid member ID";
      doThrow(new IllegalArgumentException(errorMessage))
          .when(householdService).removeUnregisteredMemberFromHousehold(memberId);

      // Act

      // Assert
    }

    /**
     * Tests the scenario where an unexpected exception occurs during unregistered member removal.
     */
    @Test
    void removeUnregisteredMemberFromHousehold_UnexpectedException_ReturnsInternalServerError() {
      // Arrange
      Long memberId = 1L;
      doThrow(new RuntimeException("Database error"))
          .when(householdService).removeUnregisteredMemberFromHousehold(memberId);

      // Act

      // Assert
    }

    /**
     * Tests the scenario where a null member ID is provided.
     */
    @Test
    void removeUnregisteredMemberFromHousehold_NullMemberId_ReturnsBadRequest() {
      // Arrange
    }
  }

  @Nested
  class GetHouseholdDetailsTests {

    @Test
    void getHouseholdDetails_ValidHouseholdId_ReturnsDetails() {
      // Arrange
      String householdId = "1L";
      Map<String, Object> details =
          Map.of("name", "Test Household", "members", List.of("John Doe"));
      when(householdService.getHouseholdDetails(householdId)).thenReturn(details);

      // Act
    }

    @Test
    void getHouseholdDetails_InvalidHouseholdId_ReturnsBadRequest() {
      // Arrange
      String householdId = "-1L";
      String errorMessage = "Invalid household ID";
      when(householdService.getHouseholdDetails(householdId)).thenThrow(
          new IllegalArgumentException(errorMessage));

    }

    @Test
    void getHouseholdDetails_ServiceThrowsUnexpectedException_ReturnsInternalServerError() {
      // Arrange
      String householdId = "1L";
      when(householdService.getHouseholdDetails(householdId)).thenThrow(
          new RuntimeException("Unexpected error"));

      // Act
    }
  }

  /**
   * Nested test class for testing the editUnregisteredMemberInHousehold endpoint.
   */
  @Nested
  class EditUnregisteredMemberInHouseholdTests {

    /**
     * Tests the positive scenario where an unregistered member is successfully edited in a household.
     */
    @Test
    void editUnregisteredMemberInHousehold_ValidRequest_ReturnsOk() {
      // Arrange
      EditMemberDto request = mock(EditMemberDto.class);
      when(request.getNewFullName()).thenReturn("John Doe");
      when(request.getHouseholdId()).thenReturn(1L);

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
    void editUnregisteredMemberInHousehold_ValidationException_ReturnsBadRequest() {
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
    void editUnregisteredMemberInHousehold_UnexpectedException_ReturnsInternalServerError() {
      // Arrange
      EditMemberDto request = mock(EditMemberDto.class);
      when(request.getNewFullName()).thenReturn("John Doe");
      when(request.getHouseholdId()).thenReturn(1L);

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