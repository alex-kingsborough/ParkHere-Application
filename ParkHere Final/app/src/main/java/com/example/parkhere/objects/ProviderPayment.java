package com.example.parkhere.objects;

import java.io.Serializable;

public class ProviderPayment implements Serializable {
    private String bank;
    private String accountNumber;
    private String routingNumber;
    private String accountType;

    public String getBank() { return bank; }
    public String getAccountNumber() { return accountNumber; }
    public String getRoutingNumber() { return routingNumber; }
    public String getAccountType() { return accountType; }

    public void setBank(String bank) { this.bank = bank; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}
