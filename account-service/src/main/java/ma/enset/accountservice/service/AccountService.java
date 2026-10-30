package ma.enset.accountservice.service;

import lombok.RequiredArgsConstructor;
import ma.enset.accountservice.entities.*;
import ma.enset.accountservice.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compte introuvable"));
    }

    public Account saveAccount(Account account) {
        account.setId(null);

        if (account.getStatus() == null) {
            account.setStatus(AccountStatus.ACTIVE);
        }

        return accountRepository.save(account);
    }
}
