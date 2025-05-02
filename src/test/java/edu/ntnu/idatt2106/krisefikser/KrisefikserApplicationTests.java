package edu.ntnu.idatt2106.krisefikser;

import edu.ntnu.idatt2106.krisefikser.config.TestConfig;
import edu.ntnu.idatt2106.krisefikser.config.TestJwtConfig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class, TestJwtConfig.class})
@ActiveProfiles("test")
class KrisefikserApplicationTests {

    @Test
    void contextLoads() {
    }
}
