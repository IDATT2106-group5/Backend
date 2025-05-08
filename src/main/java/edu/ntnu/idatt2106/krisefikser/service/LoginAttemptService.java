package edu.ntnu.idatt2106.krisefikser.service;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service to track login attempts and block users after a certain number of failed attempts.
 */

@Component
public class LoginAttemptService {

  private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);
  private static final int MAXATTEMPT = 5;
  private final ConcurrentHashMap<String, Integer> attemptsCache;

  public LoginAttemptService() {
    attemptsCache = new ConcurrentHashMap<>();
    logger.info("LoginAttemptService initialized with maximum {} attempts", MAXATTEMPT);
  }

  /**
   * Resets login attempts for a user after successful login.
   *
   * @param key The key (username or IP) to reset attempts for.
   */
  public void loginSucceeded(String key) {
    logger.debug("Successful login for key: {}", key);
    attemptsCache.remove(key);
    logger.debug("Login attempts reset for key: {}", key);
  }

  /**
   * Increments the login attempt count for a given key (e.g., username or IP address).
   *
   * @param key The key to track login attempts for.
   */
  public void loginFailed(String key) {
    int attempts = attemptsCache.getOrDefault(key, 0);
    attempts++;
    attemptsCache.put(key, attempts);

    logger.info("Failed login attempt for key: {}, current attempts: {}", key, attempts);

    if (attempts >= MAXATTEMPT) {
      logger.warn("Key {} is now blocked due to exceeding maximum login attempts", key);
    }
  }

  /**
   * Checks if a user is blocked based on failed login attempts.
   *
   * @param key The key to check.
   * @return True if the user is blocked, false otherwise.
   */
  public boolean isBlocked(String key) {
    int attempts = attemptsCache.getOrDefault(key, 0);
    boolean blocked = attempts >= MAXATTEMPT;

    if (blocked) {
      logger.debug("Key {} is blocked with {} failed attempts", key, attempts);
    } else {
      logger.trace("Key {} has {} failed attempts (not blocked)", key, attempts);
    }

    return blocked;
  }
}