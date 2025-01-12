package com.fiction;

public class RentalRecord {
    private String rentalId;
    private int customerId;
    private String customerName;
    private String atvId;
    private String startTime;
    private String endTime;
    private String status;
    private Double totalCost;
    private int rentalDuration;

    // First constructor
    public RentalRecord(String rentalId, Integer customerId, String customerName, String atvId,
                        String startTime, String endTime, String status, Double totalCost,
                        Integer rentalDuration) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.atvId = atvId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalCost = totalCost;
        this.rentalDuration = rentalDuration;
    }

    public void setTotalCost(Double totalCost) {
        if (totalCost == null) {
            throw new IllegalArgumentException("Total cost cannot be null");
        }
        this.totalCost = totalCost;
    }

    // Getters
    public String getRentalId() {
        return rentalId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAtvId() {
        return atvId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public int getRentalDuration() {
        return rentalDuration;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRawAtvId() {
        return this.atvId.split(" - ")[0];
    }
}