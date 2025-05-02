package edu.ntnu.idatt2106.krisefikser.security;

import edu.ntnu.idatt2106.krisefikser.service.CaptchaService;
import edu.ntnu.idatt2106.krisefikser.service.EmailService;
import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
@Profile("test")
public class TestConfig {

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .build();
  }

  @Bean
  public JavaMailSender javaMailSender() {
    return new JavaMailSenderImpl();
  }

  @Bean
  public EmailService emailService() {
    // Mocking email service for tests
    return Mockito.mock(EmailService.class);
  }

  @Bean
  public JwtTokenProvider jwtTokenProvider() {
    // Mocking JWT token provider for tests
    return Mockito.mock(JwtTokenProvider.class);
  }

  @Bean
  public CaptchaService captchaService() {
    // Mocking Captcha service for tests
    return Mockito.mock(CaptchaService.class);
  }
}