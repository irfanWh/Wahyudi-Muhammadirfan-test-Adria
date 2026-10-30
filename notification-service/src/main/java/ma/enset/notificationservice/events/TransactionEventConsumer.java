package ma.enset.notificationservice.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TransactionEventConsumer.class);

    @KafkaListener(topics = "bank.transactions.created")
    public void consume(TransactionCreatedEvent event) {
        LOGGER.info("Evenement TransactionCreated recu : {}", event);
    }
}
