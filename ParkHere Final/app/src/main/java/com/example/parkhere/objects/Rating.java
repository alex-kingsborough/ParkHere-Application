package com.example.parkhere.objects;

import java.io.Serializable;

public class Rating implements Serializable {
    private int rating;
    private String comment;

    public int getRating() { return rating; }
    public String getComment() { return comment; }

    public void setRating(int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
}
