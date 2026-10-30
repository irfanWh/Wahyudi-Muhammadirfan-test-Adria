package ma.enset.transactionservice.service;

import ma.enset.transactionservice.entities.BankTransaction;
import ma.enset.transactionservice.models.Account;
import ma.enset.transactionservice.models.CreateTransactionRequest;
import ma.enset.transactionservice.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountGateway accountGateway;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository, accountGateway);
        ReflectionTestUtils.setField(
                transactionService, "maxAmount", new BigDecimal("1000"));
    }

    @Test
    void createTransactionDebitsThenPersists() {
        UUID accountId = UUID.randomUUID();
        CreateTransactionRequest request = request(accountId, "250.00", "MAD");
        when(accountGateway.getAccount(accountId)).thenReturn(
                account(accountId, "ACTIVE", "MAD", 5000));
        when(transactionRepository.save(any(BankTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankTransaction result = transactionService.createTransaction(request);

        assertEquals(accountId, result.getAccountId());
        assertEquals(new BigDecimal("250.00"), result.getAmount());
        verify(accountGateway).debitAccount(
                org.mockito.ArgumentMatchers.eq(accountId), any());

        ArgumentCaptor<BankTransaction> transactionCaptor =
                ArgumentCaptor.forClass(BankTransaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals("MAD", transactionCaptor.getValue().getCurrency());
    }

    @Test
    void createTransactionRejectsAmountAboveConfiguredLimit() {
        CreateTransactionRequest request = request(
                UUID.randomUUID(), "1000.01", "MAD");

        assertThrows(ResponseStatusException.class,
                () -> transactionService.createTransaction(request));

        verify(accountGateway, never()).getAccount(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransactionRejectsBlockedAccountWithoutDebit() {
        UUID accountId = UUID.randomUUID();
        CreateTransactionRequest request = request(accountId, "250.00", "MAD");
        when(accountGateway.getAccount(accountId)).thenReturn(
                account(accountId, "BLOCKED", "MAD", 5000));

        assertThrows(ResponseStatusException.class,
                () -> transactionService.createTransaction(request));

        verify(accountGateway, never()).debitAccount(any(), any());
        verify(transactionRepository, never()).save(any());
    }

    private CreateTransactionRequest request(
            UUID accountId,
            String amount,
            String currency) {
        return new CreateTransactionRequest(
                accountId,
                new BigDecimal(amount),
                currency,
                "SHOPPING",
                "MA");
    }

    private Account account(
            UUID id,
            String status,
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
