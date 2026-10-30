package ma.enset.transactionservice;

import ma.enset.transactionservice.entities.BankTransaction;
import ma.enset.transactionservice.repositories.TransactionRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootApplication
public class TransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(TransactionRepository transactionRepository) {
        return args -> {

            UUID accountId1 = UUID.randomUUID();
            UUID accountId2 = UUID.randomUUID();

            BankTransaction t1 = new BankTransaction();
            t1.setAccountId(accountId1);
            t1.setAmount(new BigDecimal("250.00"));
            t1.setCurrency("MAD");
            t1.setMerchantCategory("SHOPPING");
            t1.setCountry("MA");
            transactionRepository.save(t1);

            BankTransaction t2 = new BankTransaction();
            t2.setAccountId(accountId1);
            t2.setAmount(new BigDecimal("100.00"));
            t2.setCurrency("MAD");
            t2.setMerchantCategory("RESTAURANT");
            t2.setCountry("MA");
            transactionRepository.save(t2);

            BankTransaction t3 = new BankTransaction();
            t3.setAccountId(accountId2);
            t3.setAmount(new BigDecimal("500.00"));
            t3.setCurrency("EUR");
            t3.setMerchantCategory("HOTEL");
            t3.setCountry("FR");
            transactionRepository.save(t3);

            BankTransaction t4 = new BankTransaction();
            t4.setAccountId(accountId1);
            t4.setAmount(new BigDecimal("75.50"));
            t4.setCurrency("MAD");
            t4.setMerchantCategory("TRANSPORT");
            t4.setCountry("MA");
            transactionRepository.save(t4);

            BankTransaction t5 = new BankTransaction();
            t5.setAccountId(accountId2);
            t5.setAmount(new BigDecimal("300.00"));
            t5.setCurrency("EUR");
            t5.setMerchantCategory("ONLINE");
            t5.setCountry("FR");
            transactionRepository.save(t5);

            transactionRepository.findAll()
                    .forEach(System.out::println);
        };
    }
}