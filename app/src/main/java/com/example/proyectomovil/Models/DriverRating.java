package com.example.proyectomovil.Models;

import java.util.Date;

public class DriverRating {
    private String Id;
    private String DriverId;
    private int Rating;
    private String Comment;
    private Date RatedAt;

    public DriverRating(String id, String driverId, int rating, String comment, Date ratedAt) {
        Id = id;
        DriverId = driverId;
        Rating = rating;
        Comment = comment;
        RatedAt = ratedAt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public Date getRatedAt() {
        return RatedAt;
    }

    public void setRatedAt(Date ratedAt) {
        RatedAt = ratedAt;
    }
}
