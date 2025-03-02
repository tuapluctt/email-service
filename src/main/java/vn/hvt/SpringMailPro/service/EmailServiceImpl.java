package vn.hvt.SpringMailPro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.model.Email;
import vn.hvt.SpringMailPro.provider.EmailProvider;
import vn.hvt.SpringMailPro.queue.EmailQueue;
import vn.hvt.SpringMailPro.template.TemplateService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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


    public EmailServiceImpl(List<EmailProvider> providers,
                            TemplateService templateService,
                            EmailQueue emailQueue) {
        // Sort providers by priority to ensure SMTP comes first
        this.providers = providers;
        this.templateService = templateService;
        this.emailQueue = emailQueue;
    }

    @Override
    public EmailResponse sendEmail(Email email) {
        // Set default from if not provider
        if (email.getFrom() == null || email.getFrom().isEmpty()) {
            email.setFrom(defaultFrom);
        }

        // Process template if specified
        if (email.getTemplateName() != null && !email.getTemplateName().isEmpty()) {
            String html = templateService.processTemplate(email.getTemplateName(), email.getTemplateModel());
            email.setHtml(html);
        }

        // Sort providers by priority
        List<EmailProvider> sortedProviders = providers.stream()
                .sorted(Comparator.comparingInt(EmailProvider::getPriority))
                .filter(EmailProvider::isAvailable)
                .collect(Collectors.toList());

        log.debug("Attempting to send email with {} providers in order: {}",
                sortedProviders.size(),
                sortedProviders.stream()
                        .map(p -> p.getName() + "(priority=" + p.getPriority() + ")")
                        .collect(Collectors.joining(", ")));

        // Try each provider in priority order until one succeeds
        for (EmailProvider provider : sortedProviders) {
            try {
                log.debug("Attempting to send email via {}", provider.getName());
                EmailResponse response = provider.sendEmail(email);
                if (response.isSuccess()) {
                    log.info("Email sent successfully via {}: {}", provider.getName(), response.getMessageId());
                    return response;
                }
            } catch (Exception e) {
                log.error("Error sending email with {}: {}", provider.getName(), e.getMessage());
                // Continue to next provider
            }
        }

        return EmailResponse.failure("All email providers failed");
    }

    @Override
    public EmailResponse sendTemplateEmail(String[] to, String templateName, Map<String, Object> model, String subject) {
        Email email = Email.builder()
                .to(to)
                .subject(subject)
                .templateName(templateName)
                .templateModel(model)
                .from(defaultFrom)
                .build();

        // If queue is enabled, add to queue
        if (queueEnabled) {
            return queueEmail(email);
        }

        return sendEmail(email);
    }

    @Override
    public List<EmailResponse> sendBulkEmails(List<Email> emails, boolean useQueue) {
        List<EmailResponse> responses = new ArrayList<>();

        if (useQueue && queueEnabled) {
            // Add all emails to queue
            for (Email email : emails) {
                responses.add(queueEmail(email));
            }
        } else {
            // Send directly one by one
            for (Email email : emails) {
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
            return EmailResponse.failure("Queue error: " + e.getMessage());
        }
    }
}
