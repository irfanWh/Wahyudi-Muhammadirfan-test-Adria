package ma.enset.transactionservice.service;

import lombok.RequiredArgsConstructor;
import ma.enset.transactionservice.entities.BankTransaction;
import ma.enset.transactionservice.models.Account;
import ma.enset.transactionservice.models.CreateTransactionRequest;
import ma.enset.transactionservice.models.DebitRequest;
import ma.enset.transactionservice.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountGateway accountGateway;

    @Value("${bank.transaction.max-amount:1000}")
    private BigDecimal maxAmount;

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

    public Account getAccountInfo(UUID accountId) {
        return accountGateway.getAccount(accountId);
    }

    public BankTransaction createTransaction(CreateTransactionRequest request) {
        validateRequest(request);

        Account account = accountGateway.getAccount(request.accountId());

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Le compte n'est pas actif");
        }

        if (!request.currency().equals(account.getCurrency())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La devise ne correspond pas au compte");
        }

        if (BigDecimal.valueOf(account.getBalance()).compareTo(request.amount()) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Solde insuffisant");
        }

        accountGateway.debitAccount(
                request.accountId(),
                new DebitRequest(request.amount(), request.currency()));

        BankTransaction transaction = new BankTransaction();
        transaction.setAccountId(request.accountId());
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setMerchantCategory(request.merchantCategory());
        transaction.setCountry(request.country());

        return transactionRepository.save(transaction);
    }

    private void validateRequest(CreateTransactionRequest request) {
        if (request == null || request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0
                || request.amount().compareTo(maxAmount) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le montant doit etre positif et inferieur ou egal a " + maxAmount);
        }

        if (request.accountId() == null
                || request.currency() == null
                || request.currency().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Le compte et la devise sont obligatoires");
        }
    }
}
