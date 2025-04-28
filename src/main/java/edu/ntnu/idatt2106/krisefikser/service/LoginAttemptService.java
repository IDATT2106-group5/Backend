package edu.ntnu.idatt2106.krisefikser.service;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Service to track login attempts and block users after a certain number of failed attempts.
 */

@Component
public class LoginAttemptService {

  private final int maxAttempt = 5;
  private final ConcurrentHashMap<String, Integer> attemptsCache;

  public LoginAttemptService() {
    attemptsCache = new ConcurrentHashMap<>();
  }

  public void loginSucceeded(String key) {
    attemptsCache.remove(key);
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
  }

  public boolean isBlocked(String key) {
    return attemptsCache.getOrDefault(key, 0) >= maxAttempt;
  }
}
