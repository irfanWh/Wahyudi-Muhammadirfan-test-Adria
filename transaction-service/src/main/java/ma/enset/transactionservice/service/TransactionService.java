package ma.enset.transactionservice.service;

import lombok.RequiredArgsConstructor;
import ma.enset.transactionservice.entities.BankTransaction;
import ma.enset.transactionservice.repositories.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import ma.enset.transactionservice.clients.AccountRestClient;
import ma.enset.transactionservice.models.Account;
import feign.FeignException;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRestClient accountRestClient;

    @CircuitBreaker(name = "accountService", fallbackMethod = "accountFallback")
    public Account getAccountInfo(UUID accountId) {
        return accountRestClient.getAccountById(accountId);
    }

    private Account accountFallback(UUID accountId, Throwable exception) {

        if (exception instanceof FeignException.NotFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Compte introuvable"
            );
        }

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Account Service est indisponible",
                exception
        );
    }

    public List<BankTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public BankTransaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Transaction introuvable"));
    }

    public List<BankTransaction> getTransactionsByAccount(UUID accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}