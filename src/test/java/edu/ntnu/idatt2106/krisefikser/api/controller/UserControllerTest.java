package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.controller.user.UserController;
import edu.ntnu.idatt2106.krisefikser.api.dto.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.StorageItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.service.StorageService;
import edu.ntnu.idatt2106.krisefikser.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @Mock
  private UserService userService;

  @Mock
  private StorageService storageService;

  @InjectMocks
  private UserController userController;

  private UserResponseDto userDto;
  private HouseholdResponseDto householdDto;
  private List<StorageItemResponseDto> storageItems;

  @BeforeEach
  void setUp() {
    userDto = new UserResponseDto("user-123", "test@example.com", "Test User", "12345678",
        Role.USER);
    householdDto = new HouseholdResponseDto("household-123", "Test Household", "Test Address",
        userDto);

    StorageItemResponseDto item1 = new StorageItemResponseDto(
        1L,
        new ItemResponseDto(1L, "Rice", 350, ItemType.FOOD),
        "household-123",
        "kg",
        2,
        LocalDateTime.now().plusDays(30)
    );

    StorageItemResponseDto item2 = new StorageItemResponseDto(
        2L,
        new ItemResponseDto(2L, "Water", 0, ItemType.LIQUIDS),
        "household-123",
        "liter",
        5,
        LocalDateTime.now().plusDays(90)
    );

    storageItems = List.of(item1, item2);
  }

  @Nested
  class GetUserTests {

    @Test
    void getUser_Success() {
      // Arrange
      when(userService.getCurrentUser()).thenReturn(userDto);

      // Act
      ResponseEntity<?> response = userController.getUser();

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(userDto, response.getBody());
      verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void getUser_IllegalArgumentException() {
      // Arrange
      String errorMessage = "No user logged in";
      when(userService.getCurrentUser()).thenThrow(new IllegalArgumentException(errorMessage));

      // Act
      ResponseEntity<?> response = userController.getUser();

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(Map.of("error", errorMessage), response.getBody());
      verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void getUser_UnexpectedException() {
      // Arrange
      when(userService.getCurrentUser()).thenThrow(new RuntimeException("Database error"));

      // Act
      ResponseEntity<?> response = userController.getUser();

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals(Map.of("error", "Internal server error"), response.getBody());
      verify(userService, times(1)).getCurrentUser();
    }
  }

  @Nested
  class GetHouseholdTests {

    @Test
    void getHousehold_Success() {
      // Arrange
      when(userService.getHousehold()).thenReturn(householdDto);

      // Act
      ResponseEntity<?> response = userController.getHousehold();

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(householdDto, response.getBody());
      verify(userService, times(1)).getHousehold();
    }

    @Test
    void getHousehold_IllegalArgumentException() {
      // Arrange
      String errorMessage = "User has no household";
      when(userService.getHousehold()).thenThrow(new IllegalArgumentException(errorMessage));

      // Act
      ResponseEntity<?> response = userController.getHousehold();

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(Map.of("error", errorMessage), response.getBody());
      verify(userService, times(1)).getHousehold();
    }

    @Test
    void getHousehold_UnexpectedException() {
      // Arrange
      when(userService.getHousehold()).thenThrow(new RuntimeException("Database error"));

      // Act
      ResponseEntity<?> response = userController.getHousehold();

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals(Map.of("error", "Internal server error"), response.getBody());
      verify(userService, times(1)).getHousehold();
    }
  }

  @Nested
  class VerifyIfMailExistsTests {

    @Test
    void verifyIfMailExists_Success() {
      // Arrange
      String email = "test@example.com";
      String userId = "user-123";
      Map<String, String> request = Map.of("email", email);
      when(userService.checkIfMailExists(email)).thenReturn(userId);

      // Act
      ResponseEntity<?> response = userController.verifyIfMailExists(request);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(Map.of("userId", userId), response.getBody());
      verify(userService, times(2)).checkIfMailExists(email);
    }

    @Test
    void verifyIfMailExists_IllegalArgumentException() {
      // Arrange
      String email = "nonexistent@example.com";
      String errorMessage = "No user with this email";
      Map<String, String> request = Map.of("email", email);
      when(userService.checkIfMailExists(email)).thenThrow(
          new IllegalArgumentException(errorMessage));

      // Act
      ResponseEntity<?> response = userController.verifyIfMailExists(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(Map.of("error", errorMessage), response.getBody());
      verify(userService, times(1)).checkIfMailExists(email);
    }

    @Test
    void verifyIfMailExists_UnexpectedException() {
      // Arrange
      String email = "test@example.com";
      Map<String, String> request = Map.of("email", email);
      when(userService.checkIfMailExists(email)).thenThrow(new RuntimeException("Database error"));

      // Act
      ResponseEntity<?> response = userController.verifyIfMailExists(request);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals(Map.of("error", "Internal server error"), response.getBody());
      verify(userService, times(1)).checkIfMailExists(email);
    }
  }

  @Nested
  class GetCurrentUserStorageItemsTests {

    @Test
    void getCurrentUserStorageItems_Success() {
      // Arrange
      when(userService.getCurrentUser()).thenReturn(userDto);
      when(userService.getHousehold()).thenReturn(householdDto);
      when(storageService.getStorageItemsByHousehold()).thenReturn(storageItems);

      // Act
      ResponseEntity<?> response = userController.getCurrentUserStorageItems();

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(storageItems, response.getBody());
      verify(userService, times(1)).getCurrentUser();
      verify(userService, times(1)).getHousehold();
      verify(storageService, times(1)).getStorageItemsByHousehold();
    }

    @Test
    void getCurrentUserStorageItems_IllegalArgumentException() {
      // Arrange
      String errorMessage = "User has no household";
      when(userService.getCurrentUser()).thenReturn(userDto);
      when(userService.getHousehold()).thenThrow(new IllegalArgumentException(errorMessage));

      // Act
      ResponseEntity<?> response = userController.getCurrentUserStorageItems();

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(Map.of("error", errorMessage), response.getBody());
      verify(userService, times(1)).getCurrentUser();
      verify(userService, times(1)).getHousehold();
      verify(storageService, never()).getStorageItemsByHousehold();
    }

    @Test
    void getCurrentUserStorageItems_UnexpectedException() {
      // Arrange
      when(userService.getCurrentUser()).thenThrow(new RuntimeException("Database error"));

      // Act
      ResponseEntity<?> response = userController.getCurrentUserStorageItems();

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals(Map.of("error", "Internal server error"), response.getBody());
      verify(userService, times(1)).getCurrentUser();
      verify(userService, never()).getHousehold();
      verify(storageService, never()).getStorageItemsByHousehold();
    }
  }
}