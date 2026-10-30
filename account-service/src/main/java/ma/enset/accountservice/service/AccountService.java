package ma.enset.accountservice.service;

import lombok.RequiredArgsConstructor;
import ma.enset.accountservice.entities.*;
import ma.enset.accountservice.models.DebitRequest;
import ma.enset.accountservice.repositories.AccountRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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

    @Transactional
    public Account debitAccount(UUID id, DebitRequest request) {
        if (request == null || request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Le montant doit etre strictement positif");
        }

        if (request.currency() == null || request.currency().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La devise est obligatoire");
        }

        Account account = accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compte introuvable"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Le compte n'est pas actif");
        }

        if (!request.currency().equals(account.getCurrency())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La devise ne correspond pas au compte");
        }

        BigDecimal balance = BigDecimal.valueOf(account.getBalance());
        if (balance.compareTo(request.amount()) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Solde insuffisant");
        }

        account.setBalance(balance.subtract(request.amount()).doubleValue());
        return accountRepository.save(account);
    }
}
