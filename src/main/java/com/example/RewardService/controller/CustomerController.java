package com.example.RewardService.controller;

import com.example.RewardService.dto.CreateCustomerResponse;
import com.example.RewardService.dto.CustomerDTO;
import com.example.RewardService.dto.CreateCustomerRequest;
import com.example.RewardService.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    public static final Logger LOGGER= LoggerFactory.getLogger(CustomerController.class);


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping("/createcustomers")
    @Operation(summary = "Register a new customer")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request) {

        LOGGER.info("Received request to create customer with name: {}",request.getName());
        CreateCustomerResponse createCustomerResponse=customerService.createCustomer(request);

        LOGGER.info("Customer created successfully with id: {}",createCustomerResponse.getCustomerId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createCustomerResponse);
    }

    @GetMapping(value = "/allCustomer")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        LOGGER.info("Received request to fetch all customers");
        List<CustomerDTO> customers =customerService.getAllCustomers();

        LOGGER.info("Successfully fetched {} customers",customers.size());

        return ResponseEntity.ok(customers);

    }


    @GetMapping("/id/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {

        LOGGER.info("Received request to fetch customer with id: {}", id);

        CustomerDTO customer =customerService.customerMapping(customerService.getCustomerEntityById(id));

        LOGGER.info("Successfully fetched customer with id: {}", id);

        return ResponseEntity.ok(customer);

    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CustomerDTO> getCustomerByName(@PathVariable String name) {
        LOGGER.info("Received request to fetch customer with name: {}", name);

        CustomerDTO customer =customerService.customerMapping(customerService.getCustomerEntityByName(name));

        LOGGER.info("Successfully fetched customer with name: {}", name);

        return ResponseEntity.ok(customer);

    }
}
