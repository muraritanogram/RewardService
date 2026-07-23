package com.example.RewardService.dto;

public class CustomerDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CustomerDTO(String address, String phone, String email, String name, Long id) {
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
