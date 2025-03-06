package vn.hvt.SpringMailPro.service;

import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.hvt.SpringMailPro.dto.EmailRequest;
import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.exception.EmailException;
import vn.hvt.SpringMailPro.exception.ErrorCode;
import vn.hvt.SpringMailPro.model.Email;
import vn.hvt.SpringMailPro.model.EmailAttachment;
import vn.hvt.SpringMailPro.provider.EmailProvider;
import vn.hvt.SpringMailPro.queue.EmailQueue;
import vn.hvt.SpringMailPro.template.TemplateService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final List<EmailProvider> providers;
    private final TemplateService templateService;
    private final EmailQueue emailQueue;

    @Value("${email.default-from}")
    private String defaultFrom;

    @Value("${email.queue.enabled:false}")
    private boolean queueEnabled;

    @Value("${email.attachment.max-size:5242880}") // Default 5MB
    private long maxAttachmentSize;



    public EmailServiceImpl(List<EmailProvider> providers,
                            TemplateService templateService,
                            EmailQueue emailQueue) {
        // Sort providers by priority to ensure SMTP comes first
        this.providers = providers;
        this.templateService = templateService;
        this.emailQueue = emailQueue;
    }

    @Override
    public EmailResponse sendEmail(EmailRequest emailRequest) {
        try{
            Email email = mapRequestToEmail(emailRequest);
            validateEmail(email);

            // List to store invalid addresses
            List<String> invalidAddresses = new ArrayList<>();


            // Validate and filter 'to' addresses
            List<String> validTo = new ArrayList<>();
            for (String address : email.getTo()) {
                if (isValidEmail(address)) {
                    validTo.add(address);
                } else {
                    invalidAddresses.add(address);
                }
            }
            if (validTo.isEmpty()) {
                throw new EmailException(ErrorCode.INVALID_EMAIL_REQUEST);
            }

            email.setTo(validTo.toArray(new String[0]));
            log.info(" invalid 'to' addresses: {}", String.join(", ", invalidAddresses));

            // Validate and filter 'cc' addresses
            if (emailRequest.getBcc() != null && emailRequest.getBcc().length > 0) {
                List<String> validCc = new ArrayList<>();
                for (String address : email.getCc()) {
                    if (isValidEmail(address)) {
                        validCc.add(address);
                    } else {
                        invalidAddresses.add(address);
                    }
                }

                if (validCc.isEmpty()) {
                    throw new EmailException(ErrorCode.INVALID_EMAIL_REQUEST);
                }

                email.setCc(validCc.toArray(new String[0]));
            }


            // Validate and filter 'bcc' addresses
            if (emailRequest.getBcc() != null && emailRequest.getBcc().length > 0) {
                List<String> validBcc = new ArrayList<>();

                for (String address : email.getBcc()) {
                    if (isValidEmail(address)) {
                        validBcc.add(address);
                    } else {
                        invalidAddresses.add(address);
                    }
                }

                if (validBcc.isEmpty()) {
                    throw new EmailException(ErrorCode.INVALID_EMAIL_REQUEST);
                }

                email.setBcc(validBcc.toArray(new String[0]));
            }

            // Set default from if not provider
            if (email.getFrom() == null || email.getFrom().isEmpty()) {
                email.setFrom(defaultFrom);
            }

            // Process template if specified
            if (email.getTemplateName() != null && !email.getTemplateName().isEmpty()) {
                try {
                    String html = templateService.processTemplate(email.getTemplateName(), email.getTemplateModel());
                    email.setHtml(html);
                } catch (Exception e) {
                    log.error("Failed to process template: {}", e.getMessage());
                    throw new EmailException(ErrorCode.TEMPLATE_ERROR, "Failed to process template: " + e.getMessage());
                }
            }

            // Sort providers by priority
            List<EmailProvider> sortedProviders = providers.stream()
                    .sorted(Comparator.comparingInt(EmailProvider::getPriority))
                    .filter(EmailProvider::isAvailable)
                    .collect(Collectors.toList());

            if (sortedProviders.isEmpty()) {
                throw new EmailException(ErrorCode.PROVIDER_NOT_AVAILABLE);
            }

            log.info("Available email providers: {}", sortedProviders.stream().map(EmailProvider::getName).collect(Collectors.joining(", ")));


            // Try each provider in priority order until one succeeds
            EmailProvider provider = selectProvider(sortedProviders);
            EmailResponse response = provider.sendEmail(email);

            log.info("Email sent successfully via {}: {}", provider.getName(), response.getMessageId());

            // Thêm danh sách địa chỉ không hợp lệ vào response
            if (!invalidAddresses.isEmpty()) {
                response.setInvalidAddresses(invalidAddresses);
            }

            return response;

        } catch (EmailException e) {
            throw  e;
        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new EmailException(ErrorCode.EMAIL_GENERAL_ERROR);
        }
    }

    private EmailProvider selectProvider(List<EmailProvider> listProviders) {
        // Tìm provider có sẵn
        for (EmailProvider provider : listProviders) {
            if (provider.isAvailable()) {
                log.debug("Selected email provider: {}", provider.getClass().getSimpleName());
                return provider;
            }
        }
        throw new EmailException(ErrorCode.PROVIDER_NOT_AVAILABLE,"All email providers failed");
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return isStrictlyValidEmail(email);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<EmailResponse> sendBulkEmails(List<EmailRequest> requests, boolean useQueue) {
        List<EmailResponse> responses = new ArrayList<>();


        List<Email> emails = requests.stream()
                .map(this::mapRequestToEmail)
                .toList();

        if (useQueue && queueEnabled) {
            // Add all emails to queue
            for (Email email : emails) {
                responses.add(queueEmail(email));
            }
        } else {
            // Send directly one by one
            for (EmailRequest email : requests) {
                responses.add(sendEmail(email));
            }
        }

        return responses;
    }

    @Override
    public EmailResponse queueEmail(Email email) {
        try {
            // Set default from if not provided
            if (email.getFrom() == null || email.getFrom().isEmpty()) {
                email.setFrom(defaultFrom);
            }

            emailQueue.addToQueue(email);
            return EmailResponse.success("QUEUED-" + System.currentTimeMillis(), "Queue");
        } catch (Exception e) {
            log.error("Failed to queue email", e);
            throw new EmailException(ErrorCode.EMAIL_GENERAL_ERROR);
        }
    }


    private void validateEmail(Email email) {
        if (email == null) {
            throw new EmailException(ErrorCode.INVALID_EMAIL_REQUEST, "Email cannot be null");
        }

        if (email.getTo() == null || email.getTo().length == 0) {
            throw new EmailException(ErrorCode.INVALID_EMAIL_REQUEST, "Recipient list cannot be empty");
        }

        if (email.getSubject() == null || email.getSubject().isEmpty()) {
            throw new EmailException(ErrorCode.INVALID_EMAIL_REQUEST, "Subject cannot be empty");
        }

        // Check if either text, html or template is provided
        boolean hasContent = (email.getText() != null && !email.getText().isEmpty()) ||
                (email.getHtml() != null && !email.getHtml().isEmpty()) ||
                (email.getTemplateName() != null && !email.getTemplateName().isEmpty());

        if (!hasContent) {
            throw new EmailException(ErrorCode.INVALID_EMAIL_REQUEST, "Email must have content (text, HTML or template)");
        }

        // Validate attachments if any
        if (email.getAttachments() != null && !email.getAttachments().isEmpty()) {
            for (EmailAttachment attachment : email.getAttachments()) {
                if (attachment.getFilename() == null || attachment.getFilename().isEmpty()) {
                    throw new EmailException(ErrorCode.ATTACHMENT_ERROR, "Attachment filename cannot be empty");
                }

                if (attachment.getContent() == null || attachment.getContent().length == 0) {
                    throw new EmailException(ErrorCode.ATTACHMENT_ERROR, "Attachment content cannot be empty");
                }

                if (attachment.getContentType() == null || attachment.getContentType().isEmpty()) {
                    throw new EmailException(ErrorCode.ATTACHMENT_ERROR, "Attachment content type cannot be empty");
                }

                if (attachment.getContent().length > maxAttachmentSize) {
                    throw new EmailException(
                            ErrorCode.ATTACHMENT_TOO_LARGE,
                            "Attachment '" + attachment.getFilename() + "' exceeds maximum size of " +
                                    (maxAttachmentSize / 1024 / 1024) + "MB"
                    );
                }
            }
        }
    }
    private Email mapRequestToEmail(EmailRequest request) {
        return Email.builder()
                .to(request.getTo())
                .cc(request.getCc())
                .bcc(request.getBcc())
                .from(request.getFrom())
                .subject(request.getSubject())
                .text(request.getText())
                .html(request.getHtml())
                .replyTo(request.getReplyTo())
                .attachments(request.getAttachments())
                .templateName(request.getTemplateName())
                .templateModel(request.getTemplateModel())
                .build();
    }
    private boolean isStrictlyValidEmail(String email) {
        String stricterEmailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(stricterEmailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
