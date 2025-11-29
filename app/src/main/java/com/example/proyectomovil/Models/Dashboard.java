package com.example.proyectomovil.Models;

import java.util.Date;

public class Dashboard {
    private String Id;
    private int TotalUsers;
    private int Delays;
    private int ConsumedTickets;
    private Double Revenue;
    private int Accidents;
    private int Reports;
    private Date CalculedAt;

    public Dashboard(String id, int totalUsers, int delays, int consumedTickets, Double revenue, int accidents, int reports, Date calculedAt) {
        Id = id;
        TotalUsers = totalUsers;
        Delays = delays;
        ConsumedTickets = consumedTickets;
        Revenue = revenue;
        Accidents = accidents;
        Reports = reports;
        CalculedAt = calculedAt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getTotalUsers() {
        return TotalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        TotalUsers = totalUsers;
    }

    public int getDelays() {
        return Delays;
    }

    public void setDelays(int delays) {
        Delays = delays;
    }

    public int getConsumedTickets() {
        return ConsumedTickets;
    }

    public void setConsumedTickets(int consumedTickets) {
        ConsumedTickets = consumedTickets;
    }

    public Double getRevenue() {
        return Revenue;
    }

    public void setRevenue(Double revenue) {
        Revenue = revenue;
    }

    public int getAccidents() {
        return Accidents;
    }

    public void setAccidents(int accidents) {
        Accidents = accidents;
    }

    public int getReports() {
        return Reports;
    }

    public void setReports(int reports) {
        Reports = reports;
    }

    public Date getCalculedAt() {
        return CalculedAt;
    }

    public void setCalculedAt(Date calculedAt) {
        CalculedAt = calculedAt;
    }
}
