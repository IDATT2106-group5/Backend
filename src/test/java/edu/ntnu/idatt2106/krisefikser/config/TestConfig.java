package edu.ntnu.idatt2106.krisefikser.config;

import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Test configuration for the application.
 *
 * <p>This configuration sets up an embedded H2 database for testing purposes.
 */

@TestConfiguration
public class TestConfig {

  /**
   * Creates an embedded H2 database for testing.
   *
   * @return the DataSource for the embedded H2 database
   */
  @Bean
  @Primary
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .build();
  }
}

