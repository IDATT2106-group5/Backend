package edu.ntnu.idatt2106.krisefikser.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for verifying hCaptcha tokens.
 */

@Service
public class CaptchaService {

  private static final String VERIFY_URL = "https://hcaptcha.com/siteverify";
  private static final Logger logger = LoggerFactory.getLogger(CaptchaService.class);

  private final RestTemplate restTemplate;

  @Value("${hcaptcha.secret}")
  private String hcaptchasecret;

  /**
   * Constructor for CaptchaService.
   *
   * @param restTemplate The RestTemplate to use for making HTTP requests.
   */
  @Autowired
  public CaptchaService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    logger.info("CaptchaService initialized");
    logger.debug("Using hCaptcha verification URL: {}", VERIFY_URL);
  }

  /**
   * Verifies the hCaptcha token.
   *
   * @param token The hCaptcha token to verify.
   * @return True if the token is valid, false otherwise.
   */
  public boolean verifyToken(String token) {
    logger.info("Verifying hCaptcha token");

    if (token == null || token.isEmpty()) {
      logger.warn("Empty or null hCaptcha token provided");
      return false;
    }

    logger.debug("Token provided for verification with length: {}", token.length());

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("secret", hcaptchasecret);
    params.add("response", token);

    logger.debug("Sending verification request to hCaptcha API");

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, params, Map.class);
      Map<String, Object> body = response.getBody();

      boolean isValid = Boolean.TRUE.equals(body.get("success"));

      if (isValid) {
        logger.info("hCaptcha token verified successfully");
        logger.debug("hCaptcha response: {}", body);
      } else {
        logger.warn("hCaptcha token verification failed. Response: {}", body);
      }

      return isValid;
    } catch (RestClientException e) {
      logger.error("Error during hCaptcha verification: {}", e.getMessage());
      return false;
    }
  }
}