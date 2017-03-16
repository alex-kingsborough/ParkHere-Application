package com.example.parkhere.objects;

public class Payment {
    private int paymentId;
    private int last4digits;

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getLast4digits() {
        return last4digits;
    }

    public void setLast4digits(int last4digits) {
        this.last4digits = last4digits;
    }
}