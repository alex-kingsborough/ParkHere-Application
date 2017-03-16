package com.example.parkhere.objects;

import java.io.Serializable;

public class ProviderReservation implements Serializable {
    private int id;
    private User seeker;
    private Vehicle seekerVehicle;
    private ProviderPayment providerPayment;
    private String startDate;
    private int startHour;
    private String endDate;
    private int endHour;
    private double totalPrice;
    private String dateReserved;

    public int getId() { return id; }
    public User getSeeker() { return seeker; }
    public Vehicle getSeekerVehicle() {
        return seekerVehicle;
    }
    public ProviderPayment getProviderPayment() {
        return providerPayment;
    }
    public String getStartDate() {
        return startDate;
    }
    public int getStartHour() {
        return startHour;
    }
    public String getEndDate() {
        return endDate;
    }
    public int getEndHour() {
        return endHour;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public String getDateReserved() { return dateReserved; }

    public void setId(int id) { this.id = id; }
    public void setSeeker(User seeker) { this.seeker = seeker; }
    public void setSeekerVehicle(Vehicle seekerVehicle) {
        this.seekerVehicle = seekerVehicle;
    }
    public void setProviderPayment(ProviderPayment providerPayment) {
        this.providerPayment = providerPayment;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public void setDateReserved(String dateReserved) { this.dateReserved = dateReserved; }
}
