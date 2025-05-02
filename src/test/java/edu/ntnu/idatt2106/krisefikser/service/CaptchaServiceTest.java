package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class CaptchaServiceTest {

  private final String TEST_SECRET_KEY = "test-secret-key";
  private final String VERIFY_URL = "https://hcaptcha.com/siteverify";
  private final String VALID_TOKEN = "valid-token";
  private final String INVALID_TOKEN = "invalid-token";

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private CaptchaService captchaService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Set the private fields using ReflectionTestUtils
    ReflectionTestUtils.setField(captchaService, "hCaptchaSecret", TEST_SECRET_KEY);

    // Mock responses for valid token
    Map<String, Object> successResponse = new HashMap<>();
    successResponse.put("success", true);
    ResponseEntity<Map> successResponseEntity = new ResponseEntity<>(successResponse,
        HttpStatus.OK);

    // Mock responses for invalid token
    Map<String, Object> failureResponse = new HashMap<>();
    failureResponse.put("success", false);
    ResponseEntity<Map> failureResponseEntity = new ResponseEntity<>(failureResponse,
        HttpStatus.OK);

    // Setup mock responses
    when(restTemplate.postForEntity(eq(VERIFY_URL), any(MultiValueMap.class), eq(Map.class)))
        .thenAnswer(invocation -> {
          MultiValueMap<String, String> params = invocation.getArgument(1);
          String token = params.getFirst("response");
          if (VALID_TOKEN.equals(token)) {
            return successResponseEntity;
          } else {
            return failureResponseEntity;
          }
        });
  }

  @Test
  void verifyToken_shouldReturnTrue_whenTokenIsValid() {
    // Act
    boolean result = captchaService.verifyToken(VALID_TOKEN);

    // Assert
    assertTrue(result, "Should return true for valid token");
  }

  @Test
  void verifyToken_shouldReturnFalse_whenTokenIsInvalid() {
    // Act
    boolean result = captchaService.verifyToken(INVALID_TOKEN);

    // Assert
    assertFalse(result, "Should return false for invalid token");
  }

  @Test
  void verifyToken_shouldReturnFalse_whenTokenIsNull() {
    // Act
    boolean result = captchaService.verifyToken(null);

    // Assert
    assertFalse(result, "Should return false for null token");
  }

  @Test
  void verifyToken_shouldReturnFalse_whenTokenIsEmpty() {
    // Act
    boolean result = captchaService.verifyToken("");

    // Assert
    assertFalse(result, "Should return false for empty token");
  }

  @Test
  void verifyToken_shouldReturnFalse_whenApiCallThrowsException() {
    // Arrange
    when(restTemplate.postForEntity(eq(VERIFY_URL), any(MultiValueMap.class), eq(Map.class)))
        .thenThrow(new RestClientException("API call failed"));

    // Act
    boolean result = captchaService.verifyToken(VALID_TOKEN);

    // Assert
    assertFalse(result, "Should return false when API call fails");
  }
}