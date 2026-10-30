package ma.enset.accountservice.mcp;

import lombok.RequiredArgsConstructor;
import ma.enset.accountservice.entities.Account;
import ma.enset.accountservice.service.AccountService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountMcpTools {

    private final AccountService accountService;

    @McpTool(name = "list_accounts", description = "List all bank accounts")
    public List<Account> listAccounts() {
        return accountService.getAllAccounts();
    }

    @McpTool(name = "get_account", description = "Get a bank account by its UUID")
    public Account getAccount(
            @McpToolParam(description = "Account UUID", required = true) String accountId) {
        return accountService.getAccountById(UUID.fromString(accountId));
    }
}
