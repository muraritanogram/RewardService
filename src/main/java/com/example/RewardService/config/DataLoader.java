package com.example.RewardService.config;

import com.example.RewardService.model.Customer;
import com.example.RewardService.model.Transaction;
import com.example.RewardService.repository.CustomerRepository;
import com.example.RewardService.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;


@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public DataLoader(CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        Customer alice = customerRepository.save(new Customer("Alice Anderson","abc@gamil.com","1234567","US street"));
        Customer bob = customerRepository.save(new Customer("Bob Brown","dcd@gamil.com","9999999","US street new 1"));
        Customer carla = customerRepository.save(new Customer("Carla Chen","mnm@gamil.com","7777777","US street 3"));


        save(alice, LocalDate.of(2026, 4, 15), "120.00");
        save(alice, LocalDate.of(2026, 4, 22), "45.00");
        save(alice, LocalDate.of(2026, 5, 3), "75.00");
        save(alice, LocalDate.of(2026, 5, 20), "200.00");
        save(alice, LocalDate.of(2026, 6, 10), "50.00");
        save(alice, LocalDate.of(2026, 6, 18), "100.50");


        save(bob, LocalDate.of(2026, 4, 5), "30.00");
        save(bob, LocalDate.of(2026, 5, 14), "150.75");
        save(bob, LocalDate.of(2026, 6, 2), "99.99");


        save(carla, LocalDate.of(2026, 4, 8), "250.00");
        save(carla, LocalDate.of(2026, 4, 28), "60.00");
        save(carla, LocalDate.of(2026, 5, 11), "500.00");
        save(carla, LocalDate.of(2026, 6, 25), "80.00");
    }

    private void save(Customer customer, LocalDate date, String amount) {
        transactionRepository.save(new Transaction(customer, date, new BigDecimal(amount)));
    }
}
