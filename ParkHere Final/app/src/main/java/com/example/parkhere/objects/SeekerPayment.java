package com.example.parkhere.objects;

import java.io.Serializable;

public class SeekerPayment implements Serializable {
    private String firstName;
    private String lastName;
    private String name;
    private String ccNumber;
    private int number;
    private String expMonth;
    private String expYear;
    private BillingAddress billingAddress;
    private int deletedWithHistory;

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getName() { return name; }
    public String getNumber() { return ccNumber; }
    public int getNum() { return number; }
    public String getExpMonth() { return expMonth; }
    public String getExpYear() { return expYear; }
    public BillingAddress getBillingAddress() { return billingAddress; }
    public int getDeletedWithHistory() { return deletedWithHistory; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setName(String name) { this.name = name; }
    public void setNumber(String ccNumber) { this.ccNumber = ccNumber; }
    public void setNumber(int number) { this.number = number; }
    public void setExpMonth(String expMonth) { this.expMonth = expMonth; }
    public void setExpYear(String expYear) { this.expYear = expYear; }
    public void setBillingAddress(BillingAddress billingAddress) { this.billingAddress = billingAddress; }
    public void setDeletedWithHistory(int deletedWithHistory) { this.deletedWithHistory = deletedWithHistory; }
}
