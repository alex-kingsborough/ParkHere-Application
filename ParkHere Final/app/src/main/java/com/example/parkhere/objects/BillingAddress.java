package com.example.parkhere.objects;

import java.io.Serializable;

public class BillingAddress implements Serializable{
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }

    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public void setCountry(String country) { this.country = country; }
}
