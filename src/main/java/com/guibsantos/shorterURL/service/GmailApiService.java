package com.guibsantos.shorterURL.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class GmailApiService {

    private final SpringTemplateEngine templateEngine;

    @Value("${gmail.client.id}")
    private String clientId;

    @Value("${gmail.client.secret}")
    private String clientSecret;

    @Value("${gmail.refresh.token}")
    private String refreshToken;

    private Gmail getGmailService() throws Exception {
        UserCredentials credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("high-performance-url-shortener")
                .build();
    }

    public void sendEmail(String to, String subject, String body, String emailType) throws Exception {
        Gmail service = getGmailService();

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress("app.shortener@gmail.com"));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        Context context = new Context();
        String templateName;

        if ("WELCOME".equalsIgnoreCase(emailType)) {
            templateName = "welcome";
            context.setVariable("username", body);
            context.setVariable("type", "WELCOME");
        } else if ("RECOVERY".equalsIgnoreCase(emailType)) {
            templateName = "recuperacao-senha";
            String[] parts = body.split(":", 2);
            context.setVariable("username", parts[0]);
            context.setVariable("code", parts[1]);
            context.setVariable("type", "RECOVERY");
        } else {
            templateName = "email-template";
            context.setVariable("body", body);
            context.setVariable("username", "Usuário");
            context.setVariable("type", "GENERIC");
        }

        String htmlContent = templateEngine.process(templateName, context);
        email.setContent(htmlContent, "text/html; charset=utf-8");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(rawMessageBytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        service.users().messages().send("me", message).execute();
    }
}
