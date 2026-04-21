package com.papertrail.service;

import com.papertrail.model.OtpChannel;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpDispatchService {

    private final JavaMailSender mailSender;
    private final String twilioSid;
    private final String twilioToken;
    private final String twilioFrom;
    private final String emailFrom;

    public OtpDispatchService(
            JavaMailSender mailSender,
            @Value("${app.twilio.account-sid}") String twilioSid,
            @Value("${app.twilio.auth-token}") String twilioToken,
            @Value("${app.twilio.from-number}") String twilioFrom,
            @Value("${spring.mail.username:}") String emailFrom
    ) {
        this.mailSender = mailSender;
        this.twilioSid = twilioSid;
        this.twilioToken = twilioToken;
        this.twilioFrom = twilioFrom;
        this.emailFrom = emailFrom;
    }

    public void dispatch(OtpChannel channel, String target, String code, String purpose) {
        if (channel == OtpChannel.SMS) {
            sendSms(target, code, purpose);
        } else {
            sendEmail(target, code, purpose);
        }
    }

    private void sendSms(String to, String code, String purpose) {
        if (twilioSid == null || twilioSid.isBlank() || twilioToken == null || twilioToken.isBlank() || twilioFrom == null || twilioFrom.isBlank()) {
            throw new IllegalStateException("Twilio is not configured. Fill TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, and TWILIO_FROM_NUMBER");
        }

        Twilio.init(twilioSid, twilioToken);
        Message.creator(
                new PhoneNumber(formatIndianMobile(to)),
                new PhoneNumber(twilioFrom),
                String.format("PaperTrail OTP for %s: %s", purpose, code)
        ).create();
    }

    private void sendEmail(String to, String code, String purpose) {
        if (emailFrom == null || emailFrom.isBlank()) {
            throw new IllegalStateException("Email sender not configured. Fill MAIL_USERNAME and MAIL_PASSWORD");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject("PaperTrail OTP - " + purpose);
        message.setText("Your PaperTrail OTP is " + code + ". It will expire shortly.");
        mailSender.send(message);
    }

    private String formatIndianMobile(String mobile) {
        String trimmed = mobile.trim();
        if (trimmed.startsWith("+")) {
            return trimmed;
        }
        if (trimmed.startsWith("0")) {
            trimmed = trimmed.substring(1);
        }
        return "+91" + trimmed;
    }
}
