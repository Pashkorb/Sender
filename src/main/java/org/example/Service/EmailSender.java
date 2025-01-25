package org.example.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String to, String subject, String body) {
        final String username = "fastmarking@yandex.ru"; // Твой email
        final String password = "otbftvbnaxdxfich"; // Пароль от почты

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.yandex.ru"); // Используем SMTP Yandex
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Письмо отправлено!");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}