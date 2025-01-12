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

    // Constructor
    public RentalRecord(String rentalId, int customerId, String customerName, String atvId, String startTime, String endTime, String status, Double totalCost, int rentalDuration) {
        System.out.println("Creating RentalRecord: " + rentalId + ", ATV ID: " + atvId);
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

    public RentalRecord(String rentalId, int customerId, String atvModel, String startTimeFormatted, String endTime, String status, double totalCost, int rentalDuration) {
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
}