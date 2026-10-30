package ma.enset.transactionservice.repositories;


import ma.enset.transactionservice.entities.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<BankTransaction, UUID> {

    List<BankTransaction> findByAccountId(UUID accountId);
}
