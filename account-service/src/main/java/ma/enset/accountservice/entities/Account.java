package ma.enset.accountservice.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String customerReference;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private String currency;

    private double balance;
}