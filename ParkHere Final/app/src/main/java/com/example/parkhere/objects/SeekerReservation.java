package com.example.parkhere.objects;

import java.io.Serializable;

public class SeekerReservation implements Serializable {
    private int id;
    private User provider;
    private Rating seekerParkingSpaceRating;
    private Rating seekerProviderRating;
    private SeekerPayment seekerPayment;
    private String startDate;
    private int startHour;
    private String endDate;
    private int endHour;
    private double totalPrice;
    private String dateReserved;
    private ParkingSpace parkingSpace;

    public int getId() { return id; }
    public User getProvider() { return provider; }
    public Rating getSeekerParkingSpaceRating() {
        return seekerParkingSpaceRating;
    }
    public Rating getSeekerProviderRating() { return seekerProviderRating; }
    public SeekerPayment getSeekerPayment() {
        return seekerPayment;
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
    public ParkingSpace getParkingSpace() { return parkingSpace; }

    public void setId(int id) { this.id = id; }
    public void setProvider(User provider) { this.provider = provider; }
    public void setSeekerParkingSpaceRating(Rating seekerParkingSpaceRating) {
        this.seekerParkingSpaceRating = seekerParkingSpaceRating;
    }
    public void setSeekerProviderRating(Rating seekerProviderRating) { this.seekerProviderRating = seekerProviderRating; }
    public void setSeekerPayment(SeekerPayment seekerPayment) {
        this.seekerPayment = seekerPayment;
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
    public void setParkingSpace(ParkingSpace parkingSpace) { this.parkingSpace = parkingSpace; }
}
