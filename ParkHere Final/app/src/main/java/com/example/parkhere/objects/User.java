package com.example.parkhere.objects;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String isProvider;
    private String isSeeker;
    private String defaultRole;
    private String govIdPicURL;
    private String profilePicURL;
    private String validationCode;
    private String isValidated;
    private String createdAt;
    private ParkingSpace[] providerParkingSpaces;

    public int getId() { return id; }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getIsProvider() {
        return isProvider;
    }
    public String getIsSeeker() {
        return isSeeker;
    }
    public String getDefaultRole() {
        return defaultRole;
    }
    public String getGovIdPicURL() { return govIdPicURL; }
    public String getProfilePicURL() {
        return profilePicURL;
    }
    public String getValidationCode() {
        return validationCode;
    }
    public String getIsValidated() {
        return isValidated;
    }
    public String getCreatedAt() { return createdAt; }
    public ParkingSpace[] getProviderParkingSpaces() { return providerParkingSpaces; }

    public void setId(int id) { this.id = id; }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }
    public void setLastName(String last_name) {
        this.lastName = last_name;
    }
    public void setPhoneNumber(String phone_number) {
        this.phoneNumber = phone_number;
    }
    public void setIsProvider(String is_provider) {
        this.isProvider = is_provider;
    }
    public void setIsSeeker(String is_seeker) {
        this.isSeeker = is_seeker;
    }
    public void setDefaultRole(String default_role) {
        this.defaultRole = default_role;
    }
    public void setGovIdPicURL(String govIdPicURL) { this.govIdPicURL = govIdPicURL;}
    public void setProfilePicURL(String profile_pic_url) {
        this.profilePicURL = profile_pic_url;
    }
    public void setValidationCode(String validation_code) {
        this.validationCode = validation_code;
    }
    public void setIsValidated(String is_validated) {
        this.isValidated = is_validated;
    }
    public void setCreatedAt(String created_at) { this.createdAt = created_at; }
    public void setProviderParkingSpaces(ParkingSpace[] providerParkingSpaces) { this.providerParkingSpaces = providerParkingSpaces; }

    //ADD TO POOJA'S
    public String getID() { return Integer.toString(id); }
}
