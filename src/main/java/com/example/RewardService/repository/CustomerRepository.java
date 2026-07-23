package com.example.RewardService.repository;


import com.example.RewardService.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNameIgnoreCase(String name);

    boolean existsByName(String name);
}
