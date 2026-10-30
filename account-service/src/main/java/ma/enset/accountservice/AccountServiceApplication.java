package ma.enset.accountservice;

import ma.enset.accountservice.entities.Account;
import ma.enset.accountservice.entities.AccountStatus;
import ma.enset.accountservice.repositories.AccountRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(AccountRepository accountRepository) {
        return args -> {

            Account a1 = new Account();
            a1.setCustomerReference("irfan");
            a1.setStatus(AccountStatus.ACTIVE);
            a1.setCurrency("MAD");
            a1.setBalance(5000);
            accountRepository.save(a1);

            Account a2 = new Account();
            a2.setCustomerReference("muhammad");
            a2.setStatus(AccountStatus.ACTIVE);
            a2.setCurrency("MAD");
            a2.setBalance(10000);
            accountRepository.save(a2);

            Account a3 = new Account();
            a3.setCustomerReference("wahyudi");
            a3.setStatus(AccountStatus.BLOCKED);
            a3.setCurrency("EUR");
            a3.setBalance(2500);
            accountRepository.save(a3);

            accountRepository.findAll()
                    .forEach(System.out::println);
        };
    }
}