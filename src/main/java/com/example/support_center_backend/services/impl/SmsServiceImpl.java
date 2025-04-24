package com.example.support_center_backend.services.impl;
import com.example.support_center_backend.models.Otp;
import com.example.support_center_backend.repository.OtpRepository;
import com.example.support_center_backend.services.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private OtpRepository otpRepository;

    @Value("${TWILIO_ACCOUNT_SID}")
    private String twilioAccountSid;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String twilioAuthToken;

    @Value("${TWILIO_PHONE_NUMBER}")
    private String twilioPhoneNumber;

    @Value("${OTP_EXPIRATION_MS}")
    private Long otpExpirationMs;

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Override
    public String sendSms(String to) {
        int code = generateOtp();
        Twilio.init(twilioAccountSid, twilioAuthToken);

        try {
            sendOtpViaTwilio(to, code);
            saveOtpInDatabase(to, code);
            logger.info("OTP sent successfully to {}", to);
            return "OTP sent successfully";
        } catch (Exception e) {
            logger.error("Error sending OTP to {}: {}", to, e.getMessage());
            return "Error sending OTP.";
        }
    }

    @Override
    public boolean verifySms(String phone, String code) {
        Otp savedOtp = otpRepository.findByPhone(phone);
        Duration expiration = Duration.ofMillis(otpExpirationMs);
        LocalDateTime expiryTime = savedOtp.getCreatedAt().plus(expiration);
        if (expiryTime.isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

    private int generateOtp() {
        return new Random().nextInt(999999) + 100000;
    }

    private void sendOtpViaTwilio(String to, int code) {
        String messageBody = String.format(
                "Your one-time password (OTP) is: %06d.",
                code
        );
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(twilioPhoneNumber),
                messageBody
        ).create();
    }

    private void saveOtpInDatabase(String to, int code) {
        Otp otp = new Otp(to, String.valueOf(code), LocalDateTime.now());
        otpRepository.save(otp);
    }
}
