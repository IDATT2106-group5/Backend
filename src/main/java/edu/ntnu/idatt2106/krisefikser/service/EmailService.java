package edu.ntnu.idatt2106.krisefikser.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * EmailService is responsible for sending emails to users. It uses JavaMailSender to send
 * confirmation emails.
 */
@Service
public class EmailService {

  private final JavaMailSender mailSender;

  /**
   * Constructor for EmailService.
   *
   * @param mailSender the JavaMailSender to use for sending emails
   */
  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * Sends a confirmation email to the user.
   *
   * @param toEmail the recipient's email address
   * @param token   the confirmation token
   */
  public void sendConfirmationEmail(String toEmail, String token) {
    String subject = "Bekreft e-post for Krisefikser";
    String confirmationUrl =
        "http://localhost:8080/api/auth/confirm?token=" + token;
    String body = "<html><body>" +
        "<h1>Velkommen til Krisefikser!</h1>" +
        "<p>Klikk på lenken under for å bekrefte e-posten din:</p>" +
        "<a href=\"" + confirmationUrl + "\">Bekreft e-post</a>" +
        "</body></html>";

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(body, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send email", e);
    }
  }
}
