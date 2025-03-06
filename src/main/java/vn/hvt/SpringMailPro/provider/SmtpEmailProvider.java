package vn.hvt.SpringMailPro.provider;


import jakarta.mail.AuthenticationFailedException;
import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.exception.EmailException;
import vn.hvt.SpringMailPro.exception.ErrorCode;
import vn.hvt.SpringMailPro.model.Email;
import vn.hvt.SpringMailPro.model.EmailAttachment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.net.ConnectException;

@Slf4j
@Component
public class SmtpEmailProvider implements EmailProvider {
    private final JavaMailSender emailSender;
    private final int priority;

    public SmtpEmailProvider(
            JavaMailSender emailSender,
            @Value("${email.smtp.priority:1}") int priority) {
        this.emailSender = emailSender;
        this.priority = priority;
    }

    @Override
    public EmailResponse sendEmail(Email email) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getTo());

            if (email.getCc() != null && email.getCc().length > 0) {
                helper.setCc(email.getCc());
            }

            if (email.getBcc() != null && email.getBcc().length > 0) {
                helper.setBcc(email.getBcc());
            }

            helper.setSubject(email.getSubject());
            helper.setFrom(email.getFrom());

            if (email.getReplyTo() != null) {
                helper.setReplyTo(email.getReplyTo());
            }

            // Set text or HTML content

            boolean hasHtml = email.getHtml() != null && !email.getHtml().isEmpty();
            helper.setText(hasHtml ? email.getHtml() : email.getText(), hasHtml);

            // Add attachments if any
            if (email.getAttachments() != null) {
                for (EmailAttachment attachment : email.getAttachments()) {
                    helper.addAttachment(
                            attachment.getFilename(),
                            new ByteArrayResource(attachment.getContent()),
                            attachment.getContentType()
                    );
                }
            }

            emailSender.send(message);
            return EmailResponse.success("SMTP-" + System.currentTimeMillis(), getName());

        } catch (AuthenticationFailedException e) {
            log.error("SMTP authentication failed", e);
            throw new EmailException(ErrorCode.SMTP_AUTHENTICATION_ERROR);
        } catch (MessagingException e) {
            log.error("Failed to send email via SMTP", e);
            throw new EmailException(ErrorCode.SMTP_SENDING_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error sending email via SMTP", e);
            throw new EmailException(ErrorCode.SMTP_SENDING_ERROR);
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            return true;
        } catch (Exception e) {
            log.warn("SMTP provider is not available", e);
            return false;
        }
    }

    @Override
    public String getName() {
        return "SMTP";
    }

    @Override
    public int getPriority() {
        return priority;
    }

}
