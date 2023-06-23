package link.signalapp.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MailingMessage {

    private String email;

    private String subject;

    private String body;

}
