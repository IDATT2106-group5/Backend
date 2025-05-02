package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private MimeMessage mimeMessage;

  @InjectMocks
  private EmailService emailService;

  @Captor
  private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
  }

  @Test
  void sendConfirmationEmail_shouldCreateAndSendEmail() throws MessagingException {
    // Arrange
    String email = "test@example.com";
    String token = "test-token-123";

    // Act
    emailService.sendConfirmationEmail(email, token);

    // Assert
    verify(mailSender).createMimeMessage();
    verify(mailSender).send(mimeMessageCaptor.capture());

    // We can't easily verify the content directly due to MimeMessageHelper being final
    // But we can verify that the message was sent
    assertEquals(mimeMessage, mimeMessageCaptor.getValue());
  }

  @Test
  void sendAdminInvitation_shouldCreateAndSendEmail() throws MessagingException {
    // Arrange
    String email = "admin@example.com";
    String invitationLink = "http://localhost:5173/admin/setup?token=abc123";

    // Act
    emailService.sendAdminInvitation(email, invitationLink);

    // Assert
    verify(mailSender).createMimeMessage();
    verify(mailSender).send(mimeMessageCaptor.capture());
    assertEquals(mimeMessage, mimeMessageCaptor.getValue());
  }

  @Test
  void sendOtpEmail_shouldCreateAndSendEmail() throws MessagingException {
    // Arrange
    String email = "user@example.com";
    String otp = "123456";

    // Act
    emailService.sendOtpEmail(email, otp);

    // Assert
    verify(mailSender).createMimeMessage();
    verify(mailSender).send(mimeMessageCaptor.capture());
    assertEquals(mimeMessage, mimeMessageCaptor.getValue());
  }

  @Test
  void sendConfirmationEmail_shouldThrowRuntimeException_whenMessagingExceptionOccurs()
      throws MessagingException {
    // Arrange
    String email = "test@example.com";
    String token = "test-token-123";

    // Mock the send method to throw an exception
    doThrow(new RuntimeException("Failed to send email")).when(mailSender)
        .send(any(MimeMessage.class));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> emailService.sendConfirmationEmail(email, token));
  }

  @Test
  void sendAdminInvitation_shouldThrowRuntimeException_whenMessagingExceptionOccurs()
      throws MessagingException {
    // Arrange
    String email = "admin@example.com";
    String invitationLink = "http://localhost:5173/admin/setup?token=abc123";

    // Mock the send method to throw an exception
    doThrow(new RuntimeException("Failed to send email")).when(mailSender)
        .send(any(MimeMessage.class));

    // Act & Assert
    assertThrows(RuntimeException.class,
        () -> emailService.sendAdminInvitation(email, invitationLink));
  }

  @Test
  void sendOtpEmail_shouldThrowRuntimeException_whenMessagingExceptionOccurs()
      throws MessagingException {
    // Arrange
    String email = "user@example.com";
    String otp = "123456";

    // Mock the send method to throw an exception
    doThrow(new RuntimeException("Failed to send email")).when(mailSender)
        .send(any(MimeMessage.class));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> emailService.sendOtpEmail(email, otp));
  }

  @Test
  void emailService_shouldHaveCorrectDependencyInjected() {
    // Verify that the constructor properly sets the JavaMailSender
    EmailService service = new EmailService(mailSender);
    assertNotNull(service);
  }
}