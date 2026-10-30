package ma.enset.accountservice.web.graphql;

import lombok.RequiredArgsConstructor;
import ma.enset.accountservice.entities.Account;
import ma.enset.accountservice.service.AccountService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphqlController {

    private final AccountService accountService;

    @QueryMapping
    public List<Account> accounts() {
        return accountService.getAllAccounts();
    }

    @QueryMapping
    public Account account(@Argument String id) {
        return accountService.getAccountById(UUID.fromString(id));
    }
}
