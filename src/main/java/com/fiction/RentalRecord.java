package com.fiction;

public class RentalRecord {
    private String rentalId;
    private String customerName;
    private String atvId; // Renamed from scooterId
    private String startTime;
    private String endTime;
    private String status;
    private Double totalCost;

    // Constructor
    public RentalRecord(String rentalId, String customerName, String atvId, String startTime, String endTime, String status, Double totalCost) {
        this.rentalId = rentalId;
        this.customerName = customerName;
        this.atvId = atvId; // Updated field name
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalCost = totalCost;
    }

    // Getters
    public String getRentalId() {
        return rentalId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAtvId() { // Updated method name
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
}
