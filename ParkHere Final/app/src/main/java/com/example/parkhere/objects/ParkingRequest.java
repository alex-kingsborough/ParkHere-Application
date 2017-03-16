package com.example.parkhere.objects;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class ParkingRequest implements Serializable{
    private LatLng location;
    private String[] dateRange;
    private int hourStart;
    private int hourEnd;
    private String[] types;
    private String weekDay;

    public LatLng getLocation() {
        return location;
    }
    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String[] getDateRange() {
        return dateRange;
    }
    public void setDateRange(String[] dateRange) {
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

    public String[] getType() {
        return types;
    }
    public void setType(String[] types) {
        this.types = types;
    }

    public String getWeekDay() { return weekDay; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }
}
