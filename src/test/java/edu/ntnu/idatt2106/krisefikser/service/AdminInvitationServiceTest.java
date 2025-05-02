package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdminInvitationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private EmailService emailService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AdminInvitationService adminInvitationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createAdminInvitation_shouldCreateUserAndSendEmail() {
    // Arrange
    String email = "admin@example.com";
    String fullName = "Admin User";

    // Act
    adminInvitationService.createAdminInvitation(email, fullName);

    // Assert
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals(email, savedUser.getEmail());
    assertEquals(fullName, savedUser.getFullName());
    assertEquals(Role.ADMIN, savedUser.getRole());
    assertNotNull(savedUser.getConfirmationToken());
    assertFalse(savedUser.isConfirmed());
    assertNotNull(savedUser.getTokenExpiry());

    verify(emailService).sendAdminInvitation(eq(email), contains(savedUser.getConfirmationToken()));
  }

  @Test
  void validateAdminSetupToken_shouldReturnTrue_whenTokenIsValid() {
    // Arrange
    String token = UUID.randomUUID().toString();
    User adminUser = new User();
    adminUser.setRole(Role.ADMIN);
    adminUser.setConfirmed(false);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 1);
    adminUser.setTokenExpiry(calendar.getTime());

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(adminUser));

    // Act
    boolean result = adminInvitationService.validateAdminSetupToken(token);

    // Assert
    assertTrue(result);
  }

  @Test
  void validateAdminSetupToken_shouldReturnFalse_whenTokenDoesNotExist() {
    // Arrange
    when(userRepository.findByConfirmationToken(anyString())).thenReturn(Optional.empty());

    // Act
    boolean result = adminInvitationService.validateAdminSetupToken("invalid-token");

    // Assert
    assertFalse(result);
  }

  @Test
  void validateAdminSetupToken_shouldReturnFalse_whenUserIsNotAdmin() {
    // Arrange
    String token = UUID.randomUUID().toString();
    User user = new User();
    user.setRole(Role.USER);
    user.setConfirmed(false);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 1);
    user.setTokenExpiry(calendar.getTime());

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(user));

    // Act
    boolean result = adminInvitationService.validateAdminSetupToken(token);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateAdminSetupToken_shouldReturnFalse_whenUserIsAlreadyConfirmed() {
    // Arrange
    String token = UUID.randomUUID().toString();
    User adminUser = new User();
    adminUser.setRole(Role.ADMIN);
    adminUser.setConfirmed(true);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 1);
    adminUser.setTokenExpiry(calendar.getTime());

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(adminUser));

    // Act
    boolean result = adminInvitationService.validateAdminSetupToken(token);

    // Assert
    assertFalse(result);
  }

  @Test
  void validateAdminSetupToken_shouldReturnFalse_whenTokenIsExpired() {
    // Arrange
    String token = UUID.randomUUID().toString();
    User adminUser = new User();
    adminUser.setRole(Role.ADMIN);
    adminUser.setConfirmed(false);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, -1);
    adminUser.setTokenExpiry(calendar.getTime());

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(adminUser));

    // Act
    boolean result = adminInvitationService.validateAdminSetupToken(token);

    // Assert
    assertFalse(result);
  }

  @Test
  void completeAdminSetup_shouldUpdateUser_whenTokenAndPasswordAreValid() {
    // Arrange
    String token = UUID.randomUUID().toString();
    String password = "ValidP@ss1";
    String encodedPassword = "encodedPassword";

    User adminUser = new User();
    adminUser.setRole(Role.ADMIN);
    adminUser.setConfirmed(false);
    adminUser.setConfirmationToken(token);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 1);
    adminUser.setTokenExpiry(calendar.getTime());

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(adminUser));
    when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

    // Act
    adminInvitationService.completeAdminSetup(token, password);

    // Assert
    assertTrue(adminUser.isConfirmed());
    assertNull(adminUser.getConfirmationToken());
    assertEquals(encodedPassword, adminUser.getPassword());
    verify(userRepository).save(adminUser);
  }

  @Test
  void completeAdminSetup_shouldThrowException_whenTokenIsInvalid() {
    // Arrange
    when(userRepository.findByConfirmationToken(anyString())).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        adminInvitationService.completeAdminSetup("invalid-token", "ValidP@ss1"));

    assertEquals("Invalid token", exception.getMessage());
  }

  @Test
  void completeAdminSetup_shouldThrowException_whenTokenIsExpired() {
    // Arrange
    String token = UUID.randomUUID().toString();
    User adminUser = new User();

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, -1);
    adminUser.setTokenExpiry(calendar.getTime());

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(adminUser));

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        adminInvitationService.completeAdminSetup(token, "ValidP@ss1"));

    assertEquals("Token has expired", exception.getMessage());
  }

  @Test
  void completeAdminSetup_shouldThrowException_whenPasswordIsTooShort() {
    // Arrange
    String token = UUID.randomUUID().toString();
    String invalidPassword = "Short1!";

    User adminUser = new User();
    adminUser.setRole(Role.ADMIN);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 1);
    adminUser.setTokenExpiry(calendar.getTime());

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(adminUser));

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        adminInvitationService.completeAdminSetup(token, invalidPassword));

    assertTrue(exception.getMessage().contains("Password must be at least 8 characters"));
  }

  @Test
  void completeAdminSetup_shouldThrowException_whenPasswordMissingRequiredCharacters() {
    // Arrange
    String token = UUID.randomUUID().toString();
    String invalidPassword = "passwordnouppercasenumbers";

    User adminUser = new User();
    adminUser.setRole(Role.ADMIN);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 1);
    adminUser.setTokenExpiry(calendar.getTime());

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(adminUser));

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        adminInvitationService.completeAdminSetup(token, invalidPassword));

    assertTrue(exception.getMessage().contains("Password must be at least 8 characters"));
  }
}