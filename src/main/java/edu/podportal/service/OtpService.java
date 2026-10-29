package edu.podportal.service;

import edu.podportal.model.Delivery;
import edu.podportal.model.DeliveryStatus;
import edu.podportal.model.OtpStatus;
import edu.podportal.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final PasswordEncoder passwordEncoder;
    private final DeliveryRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${otp.expiry.minutes:5}")
    private int expiryMinutes = 5;

    @Value("${otp.max.attempts:5}")
    private int maxAttempts = 5;

    public OtpService(PasswordEncoder passwordEncoder, DeliveryRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    public static class OtpGenerationResult {
        private final Delivery delivery;
        private final String plainOtp;

        public OtpGenerationResult(Delivery delivery, String plainOtp) {
            this.delivery = delivery;
            this.plainOtp = plainOtp;
        }

        public Delivery getDelivery() {
            return delivery;
        }

        public String getPlainOtp() {
            return plainOtp;
        }
    }

    public static class OtpVerificationResult {
        private final OtpStatus status;
        private final String message;
        private final int attemptsLeft;

        public OtpVerificationResult(OtpStatus status, String message, int attemptsLeft) {
            this.status = status;
            this.message = message;
            this.attemptsLeft = attemptsLeft;
        }

        public OtpStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public int getAttemptsLeft() {
            return attemptsLeft;
        }
    }

    public String generateSixDigitCode() {
        int code = secureRandom.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    public OtpGenerationResult generateAndAssignOtp(Delivery delivery) {
        String plainOtp = generateSixDigitCode();
        return assignExplicitOtp(delivery, plainOtp);
    }

    public OtpGenerationResult assignExplicitOtp(Delivery delivery, String plainOtp) {
        delivery.setOtpRequired(true);
        delivery.setAdminOtp(plainOtp);
        delivery.setOtpHashed(passwordEncoder.encode(plainOtp));
        delivery.setOtpExpiry(LocalDateTime.now().plusMinutes(60));
        delivery.setOtpStatus(OtpStatus.PENDING);
        delivery.setOtpAttempts(0);
        delivery.setEnteredOtp(null);
        delivery.setOtpVerifiedAt(null);

        Delivery saved = repository.save(delivery);
        return new OtpGenerationResult(saved, plainOtp);
    }


    public OtpVerificationResult verifyOtp(Long deliveryId, String agentUsername, String submittedOtp) {
        Delivery delivery = repository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        // Authorization check: Only assigned agent can verify OTP
        if (agentUsername == null || !agentUsername.equals(delivery.getAssignedAgent())) {
            throw new IllegalStateException("You can only verify OTP for your own assigned deliveries");
        }

        // Delivery must be in OUT_FOR_DELIVERY status
        if (delivery.getStatus() != DeliveryStatus.OUT_FOR_DELIVERY) {
            throw new IllegalStateException("OTP can only be verified when delivery is Out for Delivery");
        }

        // Must have OTP required
        if (!Boolean.TRUE.equals(delivery.getOtpRequired()) || delivery.getOtpHashed() == null) {
            throw new IllegalStateException("This delivery does not have an active OTP requirement");
        }

        // Cannot reuse already verified OTP
        if (delivery.getOtpStatus() == OtpStatus.VERIFIED) {
            return new OtpVerificationResult(
                    OtpStatus.VERIFIED,
                    "OTP has already been verified for this delivery and cannot be reused",
                    0
            );
        }

        int currentAttempts = delivery.getOtpAttempts() != null ? delivery.getOtpAttempts() : 0;
        if (currentAttempts >= maxAttempts) {
            delivery.setOtpStatus(OtpStatus.INCORRECT);
            repository.save(delivery);
            return new OtpVerificationResult(
                    OtpStatus.INCORRECT,
                    "Maximum verification attempts exceeded",
                    0
            );
        }

        // Validate format: exactly 6 digits
        if (submittedOtp == null || !submittedOtp.matches("^\\d{6}$")) {
            currentAttempts++;
            delivery.setOtpAttempts(currentAttempts);
            delivery.setEnteredOtp(submittedOtp);
            delivery.setOtpStatus(OtpStatus.INCORRECT);
            repository.save(delivery);
            int left = Math.max(0, maxAttempts - currentAttempts);
            return new OtpVerificationResult(
                    OtpStatus.INCORRECT,
                    "Invalid OTP format. Must be exactly 6 digits",
                    left
            );
        }

        // Check expiry
        if (delivery.getOtpExpiry() != null && LocalDateTime.now().isAfter(delivery.getOtpExpiry())) {
            delivery.setOtpStatus(OtpStatus.EXPIRED);
            delivery.setEnteredOtp(submittedOtp);
            repository.save(delivery);
            return new OtpVerificationResult(
                    OtpStatus.EXPIRED,
                    "OTP has expired",
                    0
            );
        }

        // Secure hash comparison
        boolean matches = passwordEncoder.matches(submittedOtp, delivery.getOtpHashed());
        if (matches) {
            delivery.setOtpStatus(OtpStatus.VERIFIED);
            delivery.setEnteredOtp(submittedOtp);
            delivery.setOtpVerifiedAt(LocalDateTime.now());
            repository.save(delivery);
            return new OtpVerificationResult(
                    OtpStatus.VERIFIED,
                    "OTP Verified Successfully",
                    maxAttempts - currentAttempts
            );
        } else {
            currentAttempts++;
            delivery.setOtpAttempts(currentAttempts);
            delivery.setEnteredOtp(submittedOtp);
            delivery.setOtpStatus(OtpStatus.INCORRECT);
            repository.save(delivery);
            int left = Math.max(0, maxAttempts - currentAttempts);
            return new OtpVerificationResult(
                    OtpStatus.INCORRECT,
                    "Incorrect OTP",
                    left
            );
        }
    }

    public void setExpiryMinutes(int expiryMinutes) {
        this.expiryMinutes = expiryMinutes;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
