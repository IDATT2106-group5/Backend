package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TwoFactorServiceTest {

  private final String testEmail = "test@example.com";
  private final String testOtp = "123456";
  @Mock
  private EmailService emailService;
  @InjectMocks
  private TwoFactorService twoFactorService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void generateAndSendOtp_shouldCreateAndSendSixDigitOtp() {
    // Act
    String otp = twoFactorService.generateAndSendOtp(testEmail);

    // Assert
    assertNotNull(otp);
    assertEquals(6, otp.length());
    assertTrue(otp.matches("\\d{6}"));
    verify(emailService).sendOtpEmail(eq(testEmail), anyString());
  }

  @Test
  void verifyOtp_shouldReturnTrue_whenOtpIsValidAndNotExpired() throws Exception {
    // Arrange
    setTestOtpWithExpiration(testEmail, testOtp,
        System.currentTimeMillis() + 600000); // 10 minutes in future

    // Act
    boolean result = twoFactorService.verifyOtp(testEmail, testOtp);

    // Assert
    assertTrue(result);

    // Verify OTP is removed after successful verification
    assertFalse(getOtpStorage().containsKey(testEmail));
  }

  /**
   * Helper method to set test OTP in the private storage map.
   */
  private void setTestOtpWithExpiration(String email, String otp, long expiryTime)
      throws Exception {
    Map<String, Object[]> otpStorage = getOtpStorage();
    otpStorage.put(email, new Object[]{otp, expiryTime});
  }

  /**
   * Helper method to access private otpStorage field.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object[]> getOtpStorage() throws Exception {
    Field field = TwoFactorService.class.getDeclaredField("otpStorage");
    field.setAccessible(true);
    return (Map<String, Object[]>) field.get(twoFactorService);
  }

  @Test
  void verifyOtp_shouldReturnFalse_whenOtpIsExpired() throws Exception {
    // Arrange
    setTestOtpWithExpiration(testEmail, testOtp,
        System.currentTimeMillis() - 1000); // 1 second in past

    // Act
    boolean result = twoFactorService.verifyOtp(testEmail, testOtp);

    // Assert
    assertFalse(result);

    // Verify expired OTP is removed
    assertFalse(getOtpStorage().containsKey(testEmail));
  }

  @Test
  void verifyOtp_shouldReturnFalse_whenOtpDoesNotMatch() throws Exception {
    // Arrange
    setTestOtpWithExpiration(testEmail, testOtp,
        System.currentTimeMillis() + 600000); // 10 minutes in future

    // Act
    boolean result = twoFactorService.verifyOtp(testEmail, "654321"); // Wrong OTP

    // Assert
    assertFalse(result);

    // Verify OTP is removed even after failed verification (one-time use)
    assertFalse(getOtpStorage().containsKey(testEmail));
  }

  @Test
  void verifyOtp_shouldReturnFalse_whenNoOtpExists() {
    // Act
    boolean result = twoFactorService.verifyOtp(testEmail, testOtp);

    // Assert
    assertFalse(result);
  }

  @Test
  void generateAndSendOtp_shouldOverwriteExistingOtp() throws Exception {
    // Arrange
    setTestOtpWithExpiration(testEmail, testOtp, System.currentTimeMillis() + 600000);

    // Act
    String newOtp = twoFactorService.generateAndSendOtp(testEmail);

    // Assert
    assertNotEquals(testOtp, newOtp);
    verify(emailService).sendOtpEmail(eq(testEmail), eq(newOtp));

    // Verify storage was updated with new OTP
    assertTrue(getOtpStorage().containsKey(testEmail));
    Object[] otpData = getOtpStorage().get(testEmail);
    assertEquals(newOtp, otpData[0]);
  }
}