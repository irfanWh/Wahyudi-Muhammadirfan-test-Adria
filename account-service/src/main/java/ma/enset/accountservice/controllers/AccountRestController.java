package ma.enset.accountservice.controllers;

import lombok.RequiredArgsConstructor;
import ma.enset.accountservice.entities.Account;
import ma.enset.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping
    public List<Account> allAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable UUID id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    public Account saveAccount(@RequestBody Account account) {
        return accountService.saveAccount(account);
    }
}
