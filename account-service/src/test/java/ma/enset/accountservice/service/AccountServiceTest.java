package ma.enset.accountservice.service;

import ma.enset.accountservice.entities.Account;
import ma.enset.accountservice.entities.AccountStatus;
import ma.enset.accountservice.models.DebitRequest;
import ma.enset.accountservice.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void debitAccountUpdatesBalance() {
        UUID accountId = UUID.randomUUID();
        Account account = account(accountId, AccountStatus.ACTIVE, "MAD", 5000);
        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Account result = accountService.debitAccount(
                accountId, new DebitRequest(new BigDecimal("250.00"), "MAD"));

        assertEquals(4750.0, result.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void debitAccountRejectsInsufficientBalance() {
        UUID accountId = UUID.randomUUID();
        Account account = account(accountId, AccountStatus.ACTIVE, "MAD", 100);
        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));

        assertThrows(ResponseStatusException.class, () -> accountService.debitAccount(
                accountId, new DebitRequest(new BigDecimal("250.00"), "MAD")));

        verify(accountRepository, never()).save(account);
    }

    private Account account(
            UUID id,
            AccountStatus status,
            String currency,
            double balance) {
        Account account = new Account();
        account.setId(id);
        account.setStatus(status);
        account.setCurrency(currency);
        account.setBalance(balance);
        return account;
    }
}
