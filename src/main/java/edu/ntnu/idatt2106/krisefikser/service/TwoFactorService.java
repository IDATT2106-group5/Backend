package edu.ntnu.idatt2106.krisefikser.service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Service class for handling two-factor authentication (2FA) using One-Time Passwords (OTP). This
 * service generates OTPs, sends them via email, and verifies user-provided OTPs.
 */

@Service
public class TwoFactorService {

  // OTP validity period in milliseconds (10 minutes)
  private static final long OTP_VALIDITY_PERIOD = 10 * 60 * 1000;
  private final EmailService emailService;
  private final Random random = new SecureRandom();
  // Store OTP codes with expiration time (email -> [OTP, expiration timestamp])
  private final Map<String, Object[]> otpStorage = new ConcurrentHashMap<>();

  public TwoFactorService(EmailService emailService) {
    this.emailService = emailService;
  }

  /**
   * Generates a 6-digit OTP, stores it with an expiration time, and sends it to the user's email.
   *
   * @param email The email address to send the OTP to.
   * @return The generated OTP.
   */
  public String generateAndSendOtp(String email) {
    // Generate 6-digit OTP
    String otp = String.format("%06d", random.nextInt(1000000));

    // Calculate expiration time (current time + 10 minutes)
    long expirationTime = System.currentTimeMillis() + OTP_VALIDITY_PERIOD;

    // Store OTP and its expiration time
    otpStorage.put(email, new Object[]{otp, expirationTime});

    // Send OTP via email
    emailService.sendOtpEmail(email, otp);

    return otp;
  }

  /**
   * Verifies the provided OTP against the stored OTP for the given email. If the OTP is valid and
   * not expired, it removes the OTP from storage.
   *
   * @param email       The email address associated with the OTP.
   * @param providedOtp The OTP provided by the user for verification.
   * @return True if the OTP is valid and matches the stored OTP; false otherwise.
   */
  public boolean verifyOtp(String email, String providedOtp) {
    // Get stored OTP data
    Object[] otpData = otpStorage.get(email);

    // If no OTP exists for this email
    if (otpData == null) {
      return false;
    }

    String storedOtp = (String) otpData[0];
    long expirationTime = (long) otpData[1];

    // Check if OTP has expired
    if (System.currentTimeMillis() > expirationTime) {
      // Remove expired OTP
      otpStorage.remove(email);
      return false;
    }

    // Check if provided OTP matches stored OTP
    boolean isValid = storedOtp.equals(providedOtp);

    // Remove OTP after verification attempt (one-time use)
    otpStorage.remove(email);

    return isValid;
  }
}