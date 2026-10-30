package ma.enset.transactionservice.events;

import ma.enset.transactionservice.entities.BankTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionEventPublisherTest {

    @Mock
    private KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    private TransactionEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new TransactionEventPublisher(kafkaTemplate);
    }

    @Test
    void publishesExpectedEventWithAccountIdAsKey() {
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        BankTransaction transaction = new BankTransaction();
        transaction.setId(transactionId);
        transaction.setAccountId(accountId);
        transaction.setAmount(new BigDecimal("250.00"));
        transaction.setCurrency("MAD");

        publisher.publish(transaction);

        ArgumentCaptor<TransactionCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(TransactionCreatedEvent.class);
        verify(kafkaTemplate).send(
                eq(TransactionEventPublisher.TOPIC),
                eq(accountId.toString()),
                eventCaptor.capture());

        TransactionCreatedEvent event = eventCaptor.getValue();
        assertNotNull(event.eventId());
        assertEquals("TransactionCreated", event.eventType());
        assertEquals(transactionId, event.transactionId());
        assertEquals(accountId, event.accountId());
        assertEquals(new BigDecimal("250.00"), event.amount());
        assertEquals("MAD", event.currency());
        assertNotNull(event.occurredAt());
    }
}
