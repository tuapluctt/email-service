package vn.hvt.SpringMailPro.queue;

import vn.hvt.SpringMailPro.model.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailQueue {

    private final RabbitTemplate rabbitTemplate;

    @Value("${email.queue.exchange}")
    private String exchange;

    @Value("${email.queue.routing-key}")
    private String routingKey;

    public void addToQueue(Email email) {
        log.debug("Adding email to queue: {}", email.getSubject());
        rabbitTemplate.convertAndSend(exchange, routingKey, email);
        log.debug("Email added to queue successfully");
    }
}
