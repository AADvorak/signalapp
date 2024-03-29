package link.signalapp.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

@RequiredArgsConstructor
@Component
public class MailTransport {

    private static final String ENCODING = "UTF-8";

    private final MailingSender mailingSender;

    public void send(String email, String subject, String body) throws MessagingException {
        send(new MailingMessage(email, subject, body));
    }

    private void send(MailingMessage mailingMessage) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", mailingSender.getSmtpHost());
        prop.put("mail.smtp.port", mailingSender.getSmtpPort());
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailingSender.getUsername(), mailingSender.getPassword());
                    }
                });
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailingSender.getUsername()));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(mailingMessage.getEmail())
        );
        message.setSubject(mailingMessage.getSubject(), ENCODING);
        message.setText(mailingMessage.getBody(), ENCODING);
        message.setHeader("Content-Type", "text/plain; charset=" + ENCODING);
        Transport.send(message);
    }

}