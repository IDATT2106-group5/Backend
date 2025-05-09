package edu.ntnu.idatt2106.krisefikser.service.auth;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class for handling two-factor authentication (2FA) using One-Time Passwords (OTP). This
 * service generates OTPs, sends them via email, and verifies user-provided OTPs.
 */
@Service
public class TwoFactorService {

  private static final Logger logger = LoggerFactory.getLogger(TwoFactorService.class);
  private static final long OTP_VALIDITY_PERIOD = 10 * 60 * 1000; // 10 minutes

  private final EmailService emailService;
  private final Random random = new SecureRandom();
  private final Map<String, Object[]> otpStorage = new ConcurrentHashMap<>();

  /**
   * Constructor for TwoFactorService.
   *
   * @param emailService The email service used to send OTPs.
   */
  
  public TwoFactorService(EmailService emailService) {
    this.emailService = emailService;
    logger.info("TwoFactorService instantiated. OTP validity set to {} ms", OTP_VALIDITY_PERIOD);
  }

  /**
   * Generates a 6-digit OTP, stores it with an expiration time, and sends it to the user's email.
   *
   * @param email The email address to send the OTP to.
   * @return The generated OTP.
   */
  public String generateAndSendOtp(String email) {
    logger.info("Generating OTP for email={}", email);
    String otp = String.format("%06d", random.nextInt(1_000_000));
    long expirationTime = System.currentTimeMillis() + OTP_VALIDITY_PERIOD;
    otpStorage.put(email, new Object[]{otp, expirationTime});
    logger.debug("Stored OTP={} for email={} with expiration={}", otp, email, expirationTime);

    emailService.sendOtpEmail(email, otp);
    logger.info("Sent OTP to email={}", email);

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
    logger.info("Verifying OTP for email={}", email);
    Object[] otpData = otpStorage.get(email);
    if (otpData == null) {
      logger.warn("No OTP found for email={}", email);
      return false;
    }

    String storedOtp = (String) otpData[0];
    long expirationTime = (long) otpData[1];
    long now = System.currentTimeMillis();

    if (now > expirationTime) {
      otpStorage.remove(email);
      logger.warn("OTP for email={} expired at {}, now={}", email, expirationTime, now);
      return false;
    }

    boolean isValid = storedOtp.equals(providedOtp);
    if (isValid) {
      logger.info("OTP for email={} is valid", email);
    } else {
      logger.warn("OTP for email={} is invalid (provided={}, expected={})", email, providedOtp,
          storedOtp);
    }

    otpStorage.remove(email);
    logger.debug("Removed OTP entry for email={}", email);

    return isValid;
  }
}
