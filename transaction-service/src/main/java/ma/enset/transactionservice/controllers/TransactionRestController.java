package ma.enset.transactionservice.controllers;

import lombok.RequiredArgsConstructor;
import ma.enset.transactionservice.entities.BankTransaction;
import ma.enset.transactionservice.models.CreateTransactionRequest;
import ma.enset.transactionservice.service.TransactionService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ma.enset.transactionservice.models.Account;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    @GetMapping("/check-account/{accountId}")
    public Account checkAccount(@PathVariable UUID accountId) {
        return transactionService.getAccountInfo(accountId);
    }

    @GetMapping
    public List<BankTransaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public BankTransaction getTransactionById(@PathVariable UUID id) {
        return transactionService.getTransactionById(id);
    }

    @GetMapping("/account/{accountId}")
    public List<BankTransaction> getTransactionsByAccount(
            @PathVariable UUID accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankTransaction createTransaction(
            @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }
}
