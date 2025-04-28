package edu.ntnu.idatt2106.krisefikser.security;

import edu.ntnu.idatt2106.krisefikser.service.EmailService;
import java.security.SecureRandom;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {

  private final EmailService emailService;
  private final Random random = new SecureRandom();

  public TwoFactorService(EmailService emailService) {
    this.emailService = emailService;
  }

  public String generateAndSendOtp(String email) {
    // Generate 6-digit OTP
    String otp = String.format("%06d", random.nextInt(1000000));

    // Send OTP via email
    emailService.sendOtpEmail(email, otp);

    // Store OTP for verification (possibly in Redis with expiration)
    return otp;
  }

  public boolean verifyOtp(String email, String providedOtp) {
    // Verify OTP from storage
    // ...

    return true; // Replace with actual verification
  }
}