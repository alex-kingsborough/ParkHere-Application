package com.example.parkhere.objects;

import java.io.Serializable;

public class Notification implements Serializable {
    private String message;

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
