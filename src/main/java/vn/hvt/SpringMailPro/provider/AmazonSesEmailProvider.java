package vn.hvt.SpringMailPro.provider;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.AmazonSimpleEmailServiceException;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailResult;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.exception.EmailException;
import vn.hvt.SpringMailPro.exception.ErrorCode;
import vn.hvt.SpringMailPro.model.Email;
import vn.hvt.SpringMailPro.model.EmailAttachment;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
@Component
public class AmazonSesEmailProvider implements EmailProvider {
    private final AmazonSimpleEmailService amazonSES;
    private final int priority;

    public AmazonSesEmailProvider(AmazonSimpleEmailService amazonSES, @Value("${email.aws.ses.priority}") int priority) {
        this.amazonSES = amazonSES;
        this.priority = priority;
    }

    @SneakyThrows
    @Override
    public EmailResponse sendEmail(Email email) throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);

        // Thiết lập header
        mimeMessage.setFrom(new InternetAddress(email.getFrom()));
        mimeMessage.setSubject(email.getSubject(), "UTF-8");


        InternetAddress[] addressesTo = Arrays.stream(email.getTo())
                .map(emailTo -> {
                    try {
                        return new InternetAddress(emailTo);
                    } catch (AddressException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(InternetAddress[]::new);
        mimeMessage.setRecipients(Message.RecipientType.TO, addressesTo);


        if (email.getCc() != null && email.getCc().length > 0) {
            InternetAddress[] addressesCc = Arrays.stream(email.getCc())
                    .map(emailCc -> {
                        try {
                            return new InternetAddress(emailCc);
                        } catch (AddressException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(InternetAddress[]::new);
            mimeMessage.setRecipients(Message.RecipientType.CC, addressesCc);
        }

        if (email.getBcc() != null && email.getBcc().length > 0) {
            InternetAddress[] addressesBcc = Arrays.stream(email.getBcc())
                    .map(emailBcc -> {
                        try {
                            return new InternetAddress(emailBcc);
                        } catch (AddressException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(InternetAddress[]::new);
            mimeMessage.setRecipients(Message.RecipientType.CC, addressesBcc);
        }

//        if (email.getReplyTo() != null) {
//            mimeMessage.setReplyTo(new InternetAddress[] { new InternetAddress(email.getReplyTo()) });
//        }


        // 5. Tạo multipart content (text/html + attachments)
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart contentPart = new MimeBodyPart();
        boolean hasHtml = email.getHtml() != null && !email.getHtml().isEmpty();
        if (hasHtml) {
            contentPart.setContent(email.getHtml(), "text/html; charset=UTF-8");
        } else {
            contentPart.setText(email.getText(), "UTF-8");
        }
        multipart.addBodyPart(contentPart);


        // 6. Thêm các file đính kèm nếu có
        if (email.getAttachments() != null) {
            for (EmailAttachment attachment : email.getAttachments()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setFileName(attachment.getFilename());
                attachmentPart.setContent(attachment.getContent(), attachment.getContentType());
                multipart.addBodyPart(attachmentPart);
            }
        }
        mimeMessage.setContent(multipart);


        // 7. Chuyển MimeMessage thành raw message
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mimeMessage.writeTo(outputStream);
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));


        SendRawEmailRequest request = new SendRawEmailRequest()
                .withSource(email.getFrom())
                .withRawMessage(rawMessage);

        try {
            SendRawEmailResult result = amazonSES.sendRawEmail(request);
            return EmailResponse.success("SES-" + result.getMessageId(), "AWS SES");
        } catch (AmazonSimpleEmailServiceException e) {
            log.error("Error sending email using AWS SES: {}", e.getMessage());
            throw new EmailException(ErrorCode.SES_SENDING_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new EmailException(ErrorCode.SES_SENDING_ERROR);
        } finally {
            outputStream.close();
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getName() {
        return "AWS_SES";
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
