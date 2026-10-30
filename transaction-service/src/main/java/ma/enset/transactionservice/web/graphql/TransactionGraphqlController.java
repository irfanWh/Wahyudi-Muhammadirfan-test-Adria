package ma.enset.transactionservice.web.graphql;

import lombok.RequiredArgsConstructor;
import ma.enset.transactionservice.entities.BankTransaction;
import ma.enset.transactionservice.service.TransactionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TransactionGraphqlController {

    private final TransactionService transactionService;

    @QueryMapping
    public List<BankTransaction> transactions() {
        return transactionService.getAllTransactions();
    }

    @QueryMapping
    public BankTransaction transaction(@Argument String id) {
        return transactionService.getTransactionById(UUID.fromString(id));
    }

    @QueryMapping
    public List<BankTransaction> transactionsByAccount(@Argument String accountId) {
        return transactionService.getTransactionsByAccount(UUID.fromString(accountId));
    }
}
