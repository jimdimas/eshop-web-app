package com.jimdimas.api.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationEmailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;

    private void sendEmail(String recipient,String subject,String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,"utf-8");
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content,true);

        javaMailSender.send(mimeMessage);
    }

    public void sendVerificationMail(String recipient,String verificationToken) throws MessagingException {
        String verificationLink="http://localhost:8080/api/v1/auth/verifyEmail?email="+recipient+"&verificationToken="+verificationToken;
        String content = "<p>Hello,this mail was sent by the E-Shop.</p><br>" +
                "<p>Click <a href=\""+verificationLink+"\">here</a> to verify your email.</p>";
        sendEmail(recipient,"E-Shop Email Verification",content);
    }
}
