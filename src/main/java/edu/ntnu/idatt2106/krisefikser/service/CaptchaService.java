package edu.ntnu.idatt2106.krisefikser.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Service
public class CaptchaService {

    @Value("${hcaptcha.secret}")
    private String hCaptchaSecret;

    private static final String VERIFY_URL = "https://hcaptcha.com/siteverify";

    public boolean verifyToken(String token) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", hCaptchaSecret);
        params.add("response", token);

        ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, params, Map.class);
        Map<String, Object> body = response.getBody();

        return body != null && Boolean.TRUE.equals(body.get("success"));
    }
}

