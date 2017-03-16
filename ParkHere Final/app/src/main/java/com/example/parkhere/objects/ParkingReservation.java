package com.example.parkhere.objects;

import java.io.Serializable;

public class ParkingReservation implements Serializable {
    private ParkingSpace parkingSpace;
    private int seeker_id;
    //private String startDate;
    //private String endDate;
    private String[] dateRange;
    private int hourStart;
    private int hourEnd;
    private double price;
    private String location;
    private int payment;

    public int getPayment() { return payment; }

    public void setPayment(int payment) { this.payment = payment; }

    public String getLocation() { return location; }

    public void setLocation() { this.location = location; }

    public int getSeeker_id() {
        return seeker_id;
    }

    public void setSeeker_id(int seeker_id) {
        this.seeker_id = seeker_id;
    }

    public void setParkingSpace(ParkingSpace parkingSpace) {
        this.parkingSpace = parkingSpace;
    }

    public ParkingSpace getParkingSpace() {
        return parkingSpace;
    }

//    public String getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(String startDate) {
//        this.startDate = startDate;
//    }
//
//    public String getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(String endDate) {
//        this.endDate = endDate;
//    }

    public void setDateRange(String[] dateRange) {
        this.dateRange = dateRange;
    }

    public String[] getDateRange() {
        return dateRange;
    }

    public int getHourStart() {
        return hourStart;
    }

    public void setHourStart(int hourStart) {
        this.hourStart = hourStart;
    }

    public int getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(int hourEnd) {
        this.hourEnd = hourEnd;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}