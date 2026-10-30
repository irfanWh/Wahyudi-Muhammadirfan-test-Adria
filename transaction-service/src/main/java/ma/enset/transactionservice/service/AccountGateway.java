package ma.enset.transactionservice.service;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import ma.enset.transactionservice.clients.AccountRestClient;
import ma.enset.transactionservice.models.Account;
import ma.enset.transactionservice.models.DebitRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountGateway {

    private final AccountRestClient accountRestClient;

    @CircuitBreaker(name = "accountService", fallbackMethod = "getAccountFallback")
    public Account getAccount(UUID accountId) {
        return accountRestClient.getAccountById(accountId);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "debitAccountFallback")
    public Account debitAccount(UUID accountId, DebitRequest request) {
        return accountRestClient.debitAccount(accountId, request);
    }

    private Account getAccountFallback(UUID accountId, Throwable exception) {
        if (exception instanceof FeignException.NotFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Compte introuvable");
        }

        throw serviceUnavailable(exception);
    }

    private Account debitAccountFallback(
            UUID accountId,
            DebitRequest request,
            Throwable exception) {
        if (exception instanceof FeignException.NotFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Compte introuvable");
        }

        if (exception instanceof FeignException.BadRequest) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debit refuse par Account Service");
        }

        throw serviceUnavailable(exception);
    }

    private ResponseStatusException serviceUnavailable(Throwable exception) {
        return new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Account Service est indisponible",
                exception);
    }
}
