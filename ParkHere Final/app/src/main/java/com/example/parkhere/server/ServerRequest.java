package com.example.parkhere.server;

import com.example.parkhere.objects.Availability;
import com.example.parkhere.objects.BillingAddress;
import com.example.parkhere.objects.Car;
import com.example.parkhere.objects.FilterRequest;
import com.example.parkhere.objects.Notification;
import com.example.parkhere.objects.ParkingRequest;
import com.example.parkhere.objects.ParkingReservation;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.Payment;
import com.example.parkhere.objects.ProviderPayment;
import com.example.parkhere.objects.Rating;
import com.example.parkhere.objects.SeekerPayment;
import com.example.parkhere.objects.User;
import com.example.parkhere.objects.Vehicle;

public class ServerRequest {
    private String operation;
    private User user;
    private ProviderPayment providerPayment;
    private SeekerPayment seekerPayment;
    private BillingAddress billingAddress;
    private Vehicle vehicle;
    private ParkingSpace parkingSpace;
    private Notification notification;
    private Availability availability;
    //ADD TO POOJA'S
    private ParkingRequest parkingRequest;
    private ParkingSpace[] parkingSpaces;
    private FilterRequest filterRequest;
    private ParkingReservation parkingReservation;
    private Rating[] ratings;
    private Payment payment;
    private Car car;

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setProviderPayment (ProviderPayment providerPayment) { this.providerPayment = providerPayment; }
    public void setSeekerPayment (SeekerPayment seekerPayment) { this.seekerPayment = seekerPayment; }
    public void setBillingAddress (BillingAddress billingAddress) { this.billingAddress = billingAddress; }
    public void setVehicle (Vehicle vehicle) { this.vehicle = vehicle; }
    public void setParkingSpace (ParkingSpace parkingSpace) { this.parkingSpace = parkingSpace; }
    public void setNotification (Notification notification) { this.notification = notification; }
    public void setAvailability (Availability availability) { this.availability = availability; }
    //ADD TO POOJA'S
    public void setParkingRequest(ParkingRequest parkingRequest) {
        this.parkingRequest = parkingRequest;
    }

    public void setParkingSpaces(ParkingSpace[] parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }

    public void setFilterRequest(FilterRequest filterRequest) {
        this.filterRequest = filterRequest;
    }

    public void setParkingReservation(ParkingReservation parkingReservation) {
        this.parkingReservation = parkingReservation;
    }

    public void setRatings(Rating[] ratings) {
        this.ratings = ratings;
    }

    public void setPayment(Payment payment) { this.payment = payment; }
    public void setCar(Car car) { this.car = car; }
}
