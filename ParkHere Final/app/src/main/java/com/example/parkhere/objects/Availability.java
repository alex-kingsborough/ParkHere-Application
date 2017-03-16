package com.example.parkhere.objects;

import java.io.Serializable;

public class Availability implements Serializable {
    private String startDate;
    private int startHour;
    private int endHour;
    private String pricePerHour;
    private String weekday;

    public String getStartDate() { return startDate; }
    public int getStartHour() {
        return startHour;
    }
    public int getEndHour() {
        return endHour;
    }
    public String getPricePerHour() {
        return pricePerHour;
    }
    public String getWeekday() { return weekday; }

    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }
    public void setPricePerHour(String pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
    public void setWeekday(String weekday) { this.weekday = weekday; }
}

