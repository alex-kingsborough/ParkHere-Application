package com.example.parkhere.objects;

import java.io.Serializable;

public class Vehicle implements Serializable{
    private String make;
    private String model;
    private String color;
    private String year;
    private String licensePlate;
    private int deletedWithHistory;

    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getColor() { return color; }
    public String getYear() { return year; }
    public String getLicensePlate() { return licensePlate; }
    public int getDeletedWithHistory() { return deletedWithHistory; }

    public void setMake(String make) { this.make = make; }
    public void setModel(String model) { this.model = model; }
    public void setColor(String color) { this.color = color; }
    public void setYear(String year) { this.year = year; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setDeletedWithHistory(int deletedWithHistory) { this.deletedWithHistory = deletedWithHistory; }
}
