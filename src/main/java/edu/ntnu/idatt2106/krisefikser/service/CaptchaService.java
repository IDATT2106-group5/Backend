package edu.ntnu.idatt2106.krisefikser.service;

import java.util.Map;
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
  }

  /**
   * Verifies the hCaptcha token.
   *
   * @param token The hCaptcha token to verify.
   * @return True if the token is valid, false otherwise.
   */
  public boolean verifyToken(String token) {
    if (token == null || token.isEmpty()) {
      return false;
    }

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("secret", hcaptchasecret);
    params.add("response", token);

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, params, Map.class);
      Map<String, Object> body = response.getBody();

      return body != null && Boolean.TRUE.equals(body.get("success"));
    } catch (RestClientException e) {
      return false;
    }
  }
}