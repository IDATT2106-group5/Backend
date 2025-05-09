package edu.ntnu.idatt2106.krisefikser.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * EmailService is responsible for sending emails to users. It uses JavaMailSender to send
 * confirmation emails.
 */
@Service
public class EmailService {

  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
  private final JavaMailSender mailSender;
  
  @Value("${app.mail.enabled:true}")
  private boolean mailEnabled;

  /**
   * Constructor for EmailService.
   *
   * @param mailSender the JavaMailSender to use for sending emails
   */
  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
    logger.info("EmailService initialized with mail enabled: {}", mailEnabled);
  }

  /**
   * Sends a confirmation email to the user.
   *
   * @param toEmail the recipient's email address
   * @param token   the confirmation token
   */
  public void sendConfirmationEmail(String toEmail, String token) {
    logger.info("Sending confirmation email to: {}", toEmail);

    String subject = "Bekreft e-post for Krisefikser";
    String confirmationUrl =
        "http://localhost:8080/api/auth/confirm?token=" + token;
    String body = "<html><body>"
        + "<h1>Velkommen til Krisefikser!</h1>"
        + "<p>Klikk på lenken under for å bekrefte e-posten din:</p>"
        + "<a href=\"" + confirmationUrl + "\">Bekreft e-post</a>"
        + "</body></html>";

    logger.debug("Confirmation URL generated for user: {}", toEmail);

    if (!mailEnabled) {
      logger.info("Test mode: Would send confirmation email to {} with token {}", toEmail, token);
      return;
    }

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(body, true);

      logger.debug("Attempting to send confirmation email");
      mailSender.send(message);
      logger.info("Confirmation email sent successfully to: {}", toEmail);
    } catch (MessagingException e) {
      logger.error("Failed to send confirmation email to {}: {}", toEmail, e.getMessage());
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
    logger.info("Sending admin invitation email to: {}", toEmail);

    String subject = "Admin Invitation for Krisefikser";
    String body = "<html><body>"
        + "<h1>Admin Invitation</h1>"
        + "<p>You have been invited to be an admin for Krisefikser.</p>"
        + "<p>Click the link below to set up your admin account. "
        + "This link is valid for 1 hour.</p>"
        + "<a href=\"" + invitationLink + "\">Set up admin account</a>"
        + "</body></html>";

    if (!mailEnabled) {
      logger.info("Test mode: Would send admin invitation to {} with link {}", toEmail, invitationLink);
      return;
    }

    logger.debug("Admin invitation link generated for: {}", toEmail);

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(body, true);

      logger.debug("Attempting to send admin invitation email");
      mailSender.send(message);
      logger.info("Admin invitation email sent successfully to: {}", toEmail);
    } catch (MessagingException e) {
      logger.error("Failed to send admin invitation email to {}: {}", toEmail, e.getMessage());
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
    logger.info("Sending OTP verification email to: {}", toEmail);

    String subject = "Your Krisefikser verification code";
    String body = "<html><body>"
        + "<h1>Your verification code</h1>"
        + "<p>Please use the following code to complete your login:</p>"
        + "<h2 style='font-size: 24px; background-color: #f0f0f0; padding: 10px; "
        + "display: inline-block; border-radius: 5px;'>" + otp + "</h2>"
        + "<p>This code will expire in 10 minutes.</p>"
        + "</body></html>";

    if (!mailEnabled) {
      logger.info("Test mode: Would send OTP email to {} with code {}", toEmail, otp);
      return;
    }

    logger.debug("OTP code generated for user: {}", toEmail);

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(body, true);

      logger.debug("Attempting to send OTP email");
      mailSender.send(message);
      logger.info("OTP email sent successfully to: {}", toEmail);
    } catch (MessagingException e) {
      logger.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
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
    logger.info("Sending password reset email to: {}", toEmail);

    String subject = "Tilbakestill passordet ditt – Krisefikser";
    String resetUrl = "http://localhost:5173/reset-password?token=" + token;
    String body = "<html><body>"
        + "<h1>Glemt passord?</h1>"
        + "<p>Klikk på lenken under for å tilbakestille passordet ditt. "
        + "Lenken er gyldig i 1 time:</p>"
        + "<a href=\"" + resetUrl + "\">Tilbakestill passord</a>"
        + "</body></html>";

    if (!mailEnabled) {
      logger.info("Test mode: Would send password reset email to {} with token {}", toEmail, token);
      return;
    }

    logger.debug("Password reset URL generated for user: {}", toEmail);

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(body, true);

      logger.debug("Attempting to send password reset email");
      mailSender.send(message);
      logger.info("Password reset email sent successfully to: {}", toEmail);
    } catch (MessagingException e) {
      logger.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
      throw new RuntimeException("Failed to send password reset email", e);
    }
  }
}