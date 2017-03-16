package com.example.parkhere.objects;

import java.io.Serializable;

public class ParkingSpace implements Serializable{
    private int id;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String additionalInfo;
    private String type;
    private int permitRequirement;
    private String cancellationPolicy;
    private String image;
    private double longitude;
    private double latitude;
    private int numReservations;
    private double avgRating;
    private int deletedWithHistory;
    private int numAddPics;
    private String pspic1;
    private String pspic2;
    private String pspic3;

    //ADD TO POOJA'S
    private double rating;
    private double pRating;
    private double totalPrice;
    private double distance;
    private String size;
    private int permit;
    private String additional;
    private int provider;
    private int count;
    private String weekDay;

    public int getId() { return id; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }
    public String getAdditionalInfo() { return additionalInfo; }
    public String getType() { return type; }
    public int getPermitRequirement() { return permitRequirement; }
    public String getCancellationPolicy() { return cancellationPolicy; }
    public String getImage() { return image; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }
    public int getNumReservations() { return numReservations; }
    public double getAvgRating() { return avgRating; }
    public int getDeletedWithHistory() { return deletedWithHistory; }
    public int getNumAddPics() { return numAddPics; }
    public String getPspic1() { return pspic1; }
    public String getPspic2() { return pspic2; }
    public String getPspic3() { return pspic3; }

    public void setId(int id) { this.id = id; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public void setCountry(String country) { this.country = country; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    public void setType(String type) { this.type = type; }
    public void setPermitRequirement(int permitRequirement) { this.permitRequirement = permitRequirement; }
    public void setCancellationPolicy(String cancellationPolicy) { this.cancellationPolicy = cancellationPolicy; }
    public void setImage(String image) { this.image = image; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setNumReservations(int numReservations) { this.numReservations = numReservations; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public void setDeletedWithHistory(int deletedWithHistory) { this.deletedWithHistory = deletedWithHistory; }
    public void setNumAddPics(int numAddPics) { this.numAddPics = numAddPics; }
    public void setPspic1(String pspic1) { this.pspic1 = pspic1; }
    public void setPspic2(String pspic2) { this.pspic2 = pspic2; }
    public void setPspic3(String pspic3) { this.pspic3 = pspic3; }

    //ADD TO POOJA'S
    public int getProvider() { return provider; }
    public String getSize() { return size; }
    public int getPermit() { return permit; }
    public String getAdditional() { return additional; }
    public double getLat() {return latitude; }
    public double getLong() { return longitude; }
    public double getRating() { return rating; }
    public double getpRating() { return pRating; }
    public double getTotalPrice() { return totalPrice; }
    public double getDistance() { return distance; }
    public int getCount() { return count; }
    public String getWeekDay() { return weekDay; }

    public void setProvider(int provider) { this.provider = provider; }
    public void setSize(String size) { this.size = size; }
    public void setPermit(int permit) { this.permit = permit; }
    public void setAdditional(String additional) { this.additional = additional; }
    public void setLat(double latitude) { this.latitude = latitude; }
    public void setLong(double longitude) { this.longitude = longitude; }
    public void setRating(double rating) { this.rating = rating; }
    public void setpRating(double pRating) { this.pRating = pRating; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setDistance(double distance) { this.distance = distance; }
    public void setCount(int count) { this.count = count; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay;}
}