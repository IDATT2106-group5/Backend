package edu.ntnu.idatt2106.krisefikser;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

@SpringBootApplication
public class KrisefikserApplication {

  public static void main(String[] args) {

    if (new File(".env").exists() && !isTestProfile(args)) {
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


  /**
   * Checks if the application is running with the "test" profile active.
   * 
   * This method examines the provided arguments for a specific profile setting
   * and also checks the system properties for the "spring.profiles.active" key.
   * 
   * @param args An array of command-line arguments passed to the application.
   * @return {@code true} if the "test" profile is active, either through the 
   *         command-line arguments or system properties; {@code false} otherwise.
   */
  private static boolean isTestProfile(String[] args) {
    for (String arg : args) {
      if (arg.contains("spring.profiles.active=test")) {
        return true;
      }
    }
    return System.getProperty("spring.profiles.active", "").contains("test");
  }
}
