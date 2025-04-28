package edu.ntnu.idatt2106.krisefikser;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KrisefikserApplication {

  public static void main(String[] args) {

    Dotenv dotenv = Dotenv.configure().load();

    System.setProperty("spring.datasource.url", dotenv.get("MYSQL_DATABASE_URL"));
    System.setProperty("spring.datasource.username", dotenv.get("MYSQL_USERNAME"));
    System.setProperty("spring.datasource.password", dotenv.get("MYSQL_PASSWORD"));
    System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
    System.setProperty("JWT_EXPIRATION_MS", dotenv.get("JWT_EXPIRATION_MS"));

    System.setProperty("SPRING_MAIL_HOST", dotenv.get("SPRING_MAIL_HOST"));
    System.setProperty("SPRING_MAIL_PORT", dotenv.get("SPRING_MAIL_PORT"));
    System.setProperty("SPRING_MAIL_USERNAME", dotenv.get("SPRING_MAIL_USERNAME"));
    System.setProperty("SPRING_MAIL_PASSWORD", dotenv.get("SPRING_MAIL_PASSWORD"));
    System.setProperty("SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH",
        dotenv.get("SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH"));
    System.setProperty("SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE",
        dotenv.get("SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE"));
    System.setProperty("hcaptcha.secret", dotenv.get("HCAPTCHA_SECRET"));

    SpringApplication.run(KrisefikserApplication.class, args);
  }
}
