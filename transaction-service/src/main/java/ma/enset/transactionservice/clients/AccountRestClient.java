package ma.enset.transactionservice.clients;

import ma.enset.transactionservice.models.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${services.account.url}"
)
public interface AccountRestClient {

    @GetMapping("/accounts/{id}")
    Account getAccountById(@PathVariable("id") UUID id);
}