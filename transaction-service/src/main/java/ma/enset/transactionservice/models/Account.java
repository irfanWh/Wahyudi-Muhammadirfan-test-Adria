package ma.enset.transactionservice.models;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter @Setter
public class Account {
    private UUID id;
    private String customerReference;
    private String status;
    private String currency;
    private double balance;
}