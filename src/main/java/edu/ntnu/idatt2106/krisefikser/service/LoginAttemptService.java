package edu.ntnu.idatt2106.krisefikser.service;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptService {

  private final int MAX_ATTEMPT = 5;
  private final ConcurrentHashMap<String, Integer> attemptsCache;

  public LoginAttemptService() {
    attemptsCache = new ConcurrentHashMap<>();
  }

  public void loginSucceeded(String key) {
    attemptsCache.remove(key);
  }

  public void loginFailed(String key) {
    int attempts = attemptsCache.getOrDefault(key, 0);
    attempts++;
    attemptsCache.put(key, attempts);
  }

  public boolean isBlocked(String key) {
    return attemptsCache.getOrDefault(key, 0) >= MAX_ATTEMPT;
  }
}
