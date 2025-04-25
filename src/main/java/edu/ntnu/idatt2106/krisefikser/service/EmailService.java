package edu.ntnu.idatt2106.krisefikser.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    String body = "Hei! Klikk lenken under for å bekrefte e-posten din:\n" + confirmationUrl;

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject(subject);
    message.setText(body);

    mailSender.send(message);
  }
}
