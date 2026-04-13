package com.yuosef.e_commerce.services.Impl;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${resend.api-key}")
    private String apiKey;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendOtp(String to, String otp) {
        try {
            ApiClient client = Configuration.getDefaultApiClient();
            client.setApiKey(apiKey);

            TransactionalEmailsApi api = new TransactionalEmailsApi(client);

            SendSmtpEmail email = new SendSmtpEmail();
            email.setTo(List.of(new SendSmtpEmailTo().email(to)));
            email.setSender(new SendSmtpEmailSender().email("jamalyuosef000@gmail.com").name("My App"));
            email.setSubject("Your OTP Code");
            email.setHtmlContent("""
                <div style="font-family: Arial, sans-serif;">
                    <h2>Verification Code</h2>
                    <div style="font-size: 32px; font-weight: bold; letter-spacing: 8px;
                                padding: 20px; background: #f5f5f5; text-align: center;">
                        %s
                    </div>
                    <p>This code expires in <strong>5 minutes</strong>.</p>
                </div>
            """.formatted(otp));

            api.sendTransacEmail(email);
            log.info("OTP sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP: {}", e.getMessage());
        }
    }
}