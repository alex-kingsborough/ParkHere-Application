package com.example.parkhere.objects;

public class FilterRequest {
    private int price;
    private int rating;
    private int pRating;
    private String[] dateRange;
    private int hourStart;
    private int hourEnd;
    private ParkingSpace[] parkingSpaces;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getpRating() {
        return pRating;
    }

    public void setpRating(int pRating) {
        this.pRating = pRating;
    }

    public String[] getDateRange() {
        return dateRange;
    }

    public void setDateRange(String[] date) {
        this.dateRange = dateRange;
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

    public ParkingSpace[] getParkingSpaces() {
        return parkingSpaces;
    }

    public void setParkingSpaces(ParkingSpace[] parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }
}
