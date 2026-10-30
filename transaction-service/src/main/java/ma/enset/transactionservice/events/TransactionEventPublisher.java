package ma.enset.transactionservice.events;

import lombok.RequiredArgsConstructor;
import ma.enset.transactionservice.entities.BankTransaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionEventPublisher {

    public static final String TOPIC = "bank.transactions.created";

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    public void publish(BankTransaction transaction) {
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                UUID.randomUUID(),
                "TransactionCreated",
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                Instant.now());

        kafkaTemplate.send(TOPIC, transaction.getAccountId().toString(), event);
    }
}
