package com.example.parkhere.server;

import com.example.parkhere.objects.Availability;
import com.example.parkhere.objects.Car;
import com.example.parkhere.objects.Notification;
import com.example.parkhere.objects.ParkingReservation;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.Payment;
import com.example.parkhere.objects.ProviderPayment;
import com.example.parkhere.objects.ProviderReservation;
import com.example.parkhere.objects.Rating;
import com.example.parkhere.objects.SeekerPayment;
import com.example.parkhere.objects.SeekerReservation;
import com.example.parkhere.objects.User;
import com.example.parkhere.objects.Vehicle;

public class ServerResponse {
    private String result;
    private String message;
    private User user;
    private ProviderPayment providerPayment;
    private Rating[] ratings;
    private ParkingSpace parkingSpace;
    private ParkingSpace[] parkingSpaces;
    private ProviderReservation[] providerReservations;
    private Notification[] notifications;
    private SeekerReservation[] seekerReservations;
    private Availability[] availabilities;
    private SeekerPayment[] seekerPayments;
    private Vehicle[] vehicles;
    //ADD TO POOJA'S
    private ParkingReservation[] parkingReservations;
    private Payment[] paymentOptions;
    private Car[] vehicleOptions;

    public String getResult() {
        return result;
    }
    public String getMessage() {
        return message;
    }
    public User getUser() {
        return user;
    }
    public ProviderPayment getProviderPayment() { return providerPayment; }
    public Rating[] getRatings() { return ratings; }
    public ParkingSpace getParkingSpace() { return parkingSpace; }
    public ParkingSpace[] getParkingSpaces() {return parkingSpaces; }
    public ProviderReservation[] getProviderReservations() { return providerReservations; }
    public Notification[] getNotifications() { return notifications; }
    public SeekerReservation[] getSeekerReservations() { return seekerReservations; }
    public Availability[] getAvailabilities() { return availabilities; }
    public SeekerPayment[] getSeekerPayments() { return seekerPayments; }
    public Vehicle[] getVehicles() { return vehicles; }
    //ADD TO POOJA'S
    public ParkingReservation[] getParkingReservations() { return parkingReservations; }
    public Payment[] getPaymentInformation() { return paymentOptions; }
    public Car[] getVehicleOptions() { return vehicleOptions; }
}
