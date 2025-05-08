package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class LoginAttemptServiceTest {

  private final String testEmail = "test@example.com";
  @InjectMocks
  private LoginAttemptService loginAttemptService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Clear the attempts cache before each test
    resetAttemptsCache();
  }

  private void resetAttemptsCache() {
    try {
      Field field = LoginAttemptService.class.getDeclaredField("attemptsCache");
      field.setAccessible(true);
      field.set(loginAttemptService, new ConcurrentHashMap<>());
    } catch (Exception e) {
      fail("Could not reset attempts cache: " + e.getMessage());
    }
  }

  @Test
  void loginSucceeded_shouldRemoveUserFromCache() throws Exception {
    // Arrange - Add the user to cache with some attempts
    Map<String, Integer> cache = getAttemptsCache();
    cache.put(testEmail, 3);
    assertTrue(cache.containsKey(testEmail));

    // Act
    loginAttemptService.loginSucceeded(testEmail);

    // Assert
    assertFalse(cache.containsKey(testEmail),
        "User should be removed from cache after successful login");
  }

  @SuppressWarnings("unchecked")
  private Map<String, Integer> getAttemptsCache() throws Exception {
    Field field = LoginAttemptService.class.getDeclaredField("attemptsCache");
    field.setAccessible(true);
    return (Map<String, Integer>) field.get(loginAttemptService);
  }

  @Test
  void loginFailed_shouldIncrementAttemptCounter() throws Exception {
    // Act - First failure
    loginAttemptService.loginFailed(testEmail);

    // Assert
    Map<String, Integer> cache = getAttemptsCache();
    assertEquals(1, cache.get(testEmail), "Counter should be incremented to 1 after first failure");

    // Act - Second failure
    loginAttemptService.loginFailed(testEmail);

    // Assert
    assertEquals(2, cache.get(testEmail),
        "Counter should be incremented to 2 after second failure");
  }

  @Test
  void isBlocked_shouldReturnFalse_whenAttemptsUnderMaximum() throws Exception {
    // Arrange - Set attempts below the maximum
    Map<String, Integer> cache = getAttemptsCache();
    cache.put(testEmail, 4); // maxAttempt is 5

    // Act
    boolean result = loginAttemptService.isBlocked(testEmail);

    // Assert
    assertFalse(result, "User should not be blocked when attempts are below maximum");
  }

  @Test
  void isBlocked_shouldReturnTrue_whenAttemptsEqualMaximum() throws Exception {
    // Arrange - Set attempts equal to the maximum
    Map<String, Integer> cache = getAttemptsCache();
    cache.put(testEmail, 5); // maxAttempt is 5

    // Act
    boolean result = loginAttemptService.isBlocked(testEmail);

    // Assert
    assertTrue(result, "User should be blocked when attempts reach maximum");
  }

  @Test
  void isBlocked_shouldReturnTrue_whenAttemptsExceedMaximum() throws Exception {
    // Arrange - Set attempts above the maximum
    Map<String, Integer> cache = getAttemptsCache();
    cache.put(testEmail, 6); // maxAttempt is 5

    // Act
    boolean result = loginAttemptService.isBlocked(testEmail);

    // Assert
    assertTrue(result, "User should be blocked when attempts exceed maximum");
  }

  @Test
  void isBlocked_shouldReturnFalse_whenUserNotInCache() {
    // Act - Check for a user not in the cache
    boolean result = loginAttemptService.isBlocked("nonexistent@example.com");

    // Assert
    assertFalse(result, "User not in cache should not be blocked");
  }

  @Test
  void loginFailed_shouldBlockUserAfterMaxAttempts() throws Exception {
    // Arrange
    int maxAttempt = getMaxAttempt();

    // Act - Simulate reaching maximum attempts
    for (int i = 0; i < maxAttempt; i++) {
      loginAttemptService.loginFailed(testEmail);
    }

    // Assert - User should be blocked now
    assertTrue(loginAttemptService.isBlocked(testEmail),
        "User should be blocked after maximum attempts");
    assertEquals(maxAttempt, getAttemptsCache().get(testEmail),
        "Attempts counter should equal maxAttempt");
  }

  private int getMaxAttempt() throws Exception {
    Field field = LoginAttemptService.class.getDeclaredField("MAXATTEMPT");
    field.setAccessible(true);
    return (int) field.get(loginAttemptService);
  }

  @Test
  void loginSucceeded_shouldHandleNonExistentUser() {
    // Act & Assert - Should not throw exception
    assertDoesNotThrow(() -> loginAttemptService.loginSucceeded("nonexistent@example.com"),
        "Handling successful login for non-existent user should not throw an exception");
  }

  @Test
  void integration_loginFailedAndSucceeded() throws Exception {
    // Act - Three failed attempts
    loginAttemptService.loginFailed(testEmail);
    loginAttemptService.loginFailed(testEmail);
    loginAttemptService.loginFailed(testEmail);

    // Assert - User should have 3 failed attempts
    Map<String, Integer> cache = getAttemptsCache();
    assertEquals(3, cache.get(testEmail), "User should have 3 failed attempts");
    assertFalse(loginAttemptService.isBlocked(testEmail), "User should not be blocked yet");

    // Act - Successful login
    loginAttemptService.loginSucceeded(testEmail);

    // Assert - User should be cleared from cache
    assertFalse(cache.containsKey(testEmail),
        "User should be removed from cache after successful login");
    assertFalse(loginAttemptService.isBlocked(testEmail),
        "User should not be blocked after successful login");
  }
}