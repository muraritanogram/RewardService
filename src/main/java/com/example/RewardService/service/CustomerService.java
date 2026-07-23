package com.example.RewardService.service;


import com.example.RewardService.dto.CreateCustomerResponse;
import com.example.RewardService.dto.CustomerDTO;
import com.example.RewardService.dto.CreateCustomerRequest;
import com.example.RewardService.model.Customer;

import java.util.List;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();

    Customer getCustomerEntityById(Long id);

    Customer getCustomerEntityByName(String name);

    CustomerDTO customerMapping(Customer customer);

    CreateCustomerResponse createCustomer(CreateCustomerRequest request);
}
