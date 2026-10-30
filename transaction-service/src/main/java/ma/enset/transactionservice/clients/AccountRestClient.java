package ma.enset.transactionservice.clients;

import ma.enset.transactionservice.models.Account;
import ma.enset.transactionservice.models.DebitRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${services.account.url}"
)
public interface AccountRestClient {

    @GetMapping("/accounts/{id}")
    Account getAccountById(@PathVariable("id") UUID id);

    @PostMapping("/accounts/{id}/debit")
    Account debitAccount(
            @PathVariable("id") UUID id,
            @RequestBody DebitRequest request);
}
