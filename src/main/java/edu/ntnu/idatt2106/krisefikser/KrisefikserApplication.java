package edu.ntnu.idatt2106.krisefikser;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

@SpringBootApplication
public class KrisefikserApplication {

  public static void main(String[] args) {
    boolean isTestProfile = isTestProfile(args);

    if (isTestProfile) {
      System.setProperty("spring.mail.host", "localhost");
      System.setProperty("spring.mail.port", "3025");
      System.setProperty("spring.mail.username", "test");
      System.setProperty("spring.mail.password", "test");
      System.setProperty("app.mail.enabled", "false");
    }
    
    if (new File(".env").exists() && !isTestProfile) {
      try {
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
      } catch (Exception e) {
        System.out.println("Warning: Failed to load .env file, continuing with defaults or environment variables");
      }
    }

    SpringApplication.run(KrisefikserApplication.class, args);
  }

  private static boolean isTestProfile(String[] args) {
    for (String arg : args) {
      if (arg.contains("spring.profiles.active=test")) {
        return true;
      }
    }
    
    if (System.getProperty("spring.profiles.active", "").contains("test")) {
      return true;
    }
    
    if (System.getProperty("spring-boot.run.profiles", "").contains("test")) {
      return true;
    }
    
    return false;
  }
}
