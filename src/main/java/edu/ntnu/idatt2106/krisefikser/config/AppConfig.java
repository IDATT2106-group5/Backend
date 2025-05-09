package edu.ntnu.idatt2106.krisefikser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for the application.
 *
 * <p>This class contains beans and configurations that are used throughout the application.<p></p>
 */

@Configuration
public class AppConfig {

  /**
   * Creates a RestTemplate bean.
   *
   * @return A RestTemplate bean.
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}