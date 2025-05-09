package edu.ntnu.idatt2106.krisefikser;

import edu.ntnu.idatt2106.krisefikser.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * This is a test class for the KrisefikserApplication. It loads the application context and
 * verifies that it starts up correctly.
 */

@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles("test")
public class KrisefikserApplicationTests {

  @Test
  void contextLoads() {
  }
}
