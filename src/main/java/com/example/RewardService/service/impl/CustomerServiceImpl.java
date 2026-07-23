package com.example.RewardService.service.impl;


import com.example.RewardService.dto.CreateCustomerResponse;
import com.example.RewardService.dto.CustomerDTO;
import com.example.RewardService.exception.ResourceNotFoundException;
import com.example.RewardService.dto.CreateCustomerRequest;
import com.example.RewardService.model.Customer;
import com.example.RewardService.repository.CustomerRepository;
import com.example.RewardService.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository)
    {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::customerMapping)
                .toList();
    }

    @Override
    public Customer getCustomerEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public Customer getCustomerEntityByName(String name) {
        return customerRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with name: " + name));
    }

    @Override
    public CustomerDTO customerMapping(Customer customer) {
        return new CustomerDTO(customer.getAddress(),customer.getPhoneNo(),customer.getEmail(), customer.getName(),customer.getId());
    }

    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerRequest request)
    {
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNo(request.getPhoneNo())
                .address(request.getAddress())
                .build();

        customer = customerRepository.save(customer);

        return CreateCustomerResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNo(customer.getPhoneNo())
                .address(customer.getAddress())
                .build();

    }
}
