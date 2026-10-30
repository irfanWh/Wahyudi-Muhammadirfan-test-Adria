package ma.enset.notificationservice.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class TransactionEventConsumerTest {

    @Test
    void logsReceivedEvent(CapturedOutput output) {
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                UUID.randomUUID(),
                "TransactionCreated",
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("250.00"),
                "MAD",
                Instant.now());

        new TransactionEventConsumer().consume(event);

        assertThat(output)
                .contains("Evenement TransactionCreated recu")
                .contains(event.transactionId().toString());
    }
}
