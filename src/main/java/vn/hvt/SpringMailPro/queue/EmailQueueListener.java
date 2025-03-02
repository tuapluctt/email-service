package vn.hvt.SpringMailPro.queue;

import vn.hvt.SpringMailPro.model.Email;
import vn.hvt.SpringMailPro.provider.EmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailQueueListener {

    private final List<EmailProvider> providers;

    @RabbitListener(queues = "${email.queue.name}")
    public void processQueuedEmail(Email email) {
        log.debug("Processing queued email: {}", email.getSubject());

        // Try each provider until one succeeds
        for (EmailProvider provider : providers) {
            try {
                if (provider.isAvailable()) {
                    log.debug("Attempting to send queued email via {}", provider.getName());
                    var response = provider.sendEmail(email);
                    if (response.isSuccess()) {
                        log.info("Queued email sent successfully via {}: {}",
                                provider.getName(), response.getMessageId());
                        return;
                    }
                }
            } catch (Exception e) {
                log.error("Error sending queued email with {}: {}", provider.getName(), e.getMessage());
                // Continue to next provider
            }
        }

        log.error("All email providers failed for queued email: {}", email.getSubject());
    }
}
