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
    String body = "<html><body>"
        + "<h1>Velkommen til Krisefikser!</h1>"
        + "<p>Klikk på lenken under for å bekrefte e-posten din:</p>"
        + "<a href=\"" + confirmationUrl + "\">Bekreft e-post</a>"
        + "</body></html>";

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

  /**
   * Sends an invitation email to a new admin user.
   *
   * @param toEmail        the recipient's email address
   * @param invitationLink the link for admin account setup
   */
  public void sendAdminInvitation(String toEmail, String invitationLink) {
    String subject = "Admin Invitation for Krisefikser";
    String body = "<html><body>"
        + "<h1>Admin Invitation</h1>"
        + "<p>You have been invited to be an admin for Krisefikser.</p>"
        + "<p>Click the link below to set up your admin account. "
        + "This link is valid for 1 hour.</p>"
        + "<a href=\"" + invitationLink + "\">Set up admin account</a>"
        + "</body></html>";

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(body, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send admin invitation email", e);
    }
  }

  /**
   * Sends a one-time password (OTP) email for two-factor authentication.
   *
   * @param toEmail the recipient's email address
   * @param otp     the one-time password
   */
  public void sendOtpEmail(String toEmail, String otp) {
    String subject = "Your Krisefikser verification code";
    String body = "<html><body>"
        + "<h1>Your verification code</h1>"
        + "<p>Please use the following code to complete your login:</p>"
        + "<h2 style='font-size: 24px; background-color: #f0f0f0; padding: 10px; "
        + "display: inline-block; border-radius: 5px;'>" + otp + "</h2>"
        + "<p>This code will expire in 10 minutes.</p>"
        + "</body></html>";

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(body, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send verification code", e);
    }
  }

  /**
   * Sends a password reset email to the user.
   *
   * @param toEmail the recipient's email address
   * @param token   the reset token
   */
  public void sendPasswordResetEmail(String toEmail, String token) {
    String subject = "Tilbakestill passordet ditt – Krisefikser";
    String resetUrl = "http://localhost:5173/reset-password?token=" + token;
    String body = "<html><body>"
        + "<h1>Glemt passord?</h1>"
        + "<p>Klikk på lenken under for å tilbakestille passordet ditt. Lenken er gyldig i 1 time:</p>"
        + "<a href=\"" + resetUrl + "\">Tilbakestill passord</a>"
        + "</body></html>";

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(body, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send password reset email", e);
    }
  }
}
