package com.example.proyectomovil.Models;

import java.util.Date;

public class Driver {
    private String Id;
    private String FirstName;
    private String LastName;
    private String Email;
    private String Phone;
    private String PasswordHash;
    private Double Rating;
    private String AssignedBusId;
    private String Role;
    private Date CreatedAt;

    public Driver(String id, String firstName, String lastName, String email, String phone, String passwordHash, Double rating, String assignedBusId, String role, Date createdAt) {
        Id = id;
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        Phone = phone;
        PasswordHash = passwordHash;
        Rating = rating;
        AssignedBusId = assignedBusId;
        Role = role;
        CreatedAt = createdAt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPasswordHash() {
        return PasswordHash;
    }

    public void setPasswordHash(String passwordHash) {
        PasswordHash = passwordHash;
    }

    public Double getRating() {
        return Rating;
    }

    public void setRating(Double rating) {
        Rating = rating;
    }

    public String getAssignedBusId() {
        return AssignedBusId;
    }

    public void setAssignedBusId(String assignedBusId) {
        AssignedBusId = assignedBusId;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        CreatedAt = createdAt;
    }
}
