package com.fiction;

public class RentalRecord {
    private String rentalId;
    private String customerName;
    private String atvId;
    private String startTime;
    private String endTime;
    private String status;
    private Double totalCost;
    private int rentalDuration; // Add this field

    // Constructor
    public RentalRecord(String rentalId, String customerName, String atvId, String startTime, String endTime, String status, Double totalCost, int rentalDuration) {
        System.out.println("Creating RentalRecord: " + rentalId + ", ATV ID: " + atvId);
        this.rentalId = rentalId;
        this.customerName = customerName;
        this.atvId = atvId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalCost = totalCost;
        this.rentalDuration = rentalDuration; // Initialize this field
    }

    // Getters
    public String getRentalId() {
        return rentalId;
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
        return rentalDuration; // Add this getter
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }
}