package com.example.RewardService.dto;

/**
 * Response returned after creating a new customer.
 * Reward points will always be 0 at the time of creation.
 */
public class CreateCustomerResponse {

    private final Long customerId;
    private final String name;
    private final String email;
    private final String phoneNo;
    private final String address;
    private final int rewardPoints;

    private CreateCustomerResponse(Builder builder) {
        this.customerId = builder.customerId;
        this.name = builder.name;
        this.email = builder.email;
        this.phoneNo = builder.phoneNo;
        this.address = builder.address;
        this.rewardPoints = builder.rewardPoints;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long customerId;
        private String name;
        private String email;
        private String phoneNo;
        private String address;
        private int rewardPoints;

        public Builder customerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder rewardPoints(int rewardPoints) {
            this.rewardPoints = rewardPoints;
            return this;
        }

        public CreateCustomerResponse build() {
            return new CreateCustomerResponse(this);
        }
    }
}
