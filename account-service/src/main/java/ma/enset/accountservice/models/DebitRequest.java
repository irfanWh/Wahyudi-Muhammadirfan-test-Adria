package ma.enset.accountservice.models;

import java.math.BigDecimal;

public record DebitRequest(BigDecimal amount, String currency) {
}
