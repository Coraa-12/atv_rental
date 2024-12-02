package com.fiction;

public class RentalRecord {
    private String rentalId;
    private String customerName;
    private String scooterId;
    private String startTime;
    private String endTime;
    private String status;
    private Double totalCost;

    public RentalRecord(String rentalId, String customerName, String scooterId, String startTime, String endTime, String status, Double totalCost) {
        this.rentalId = rentalId;
        this.customerName = customerName;
        this.scooterId = scooterId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalCost = totalCost;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getScooterId() {
        return scooterId;
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
