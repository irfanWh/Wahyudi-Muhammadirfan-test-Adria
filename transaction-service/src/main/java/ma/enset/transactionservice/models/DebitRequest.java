package ma.enset.transactionservice.models;

import java.math.BigDecimal;

public record DebitRequest(BigDecimal amount, String currency) {
}
