package ma.enset.transactionservice.mcp;

import lombok.RequiredArgsConstructor;
import ma.enset.transactionservice.entities.BankTransaction;
import ma.enset.transactionservice.service.TransactionService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMcpTools {

    private final TransactionService transactionService;

    @McpTool(name = "list_transactions", description = "List all bank transactions")
    public List<BankTransaction> listTransactions() {
        return transactionService.getAllTransactions();
    }

    @McpTool(
            name = "get_transactions_by_account",
            description = "List transactions for a bank account")
    public List<BankTransaction> getTransactionsByAccount(
            @McpToolParam(description = "Account UUID", required = true) String accountId) {
        return transactionService.getTransactionsByAccount(UUID.fromString(accountId));
    }
}
