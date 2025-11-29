package com.example.proyectomovil.Models;

import java.util.Date;

public class Trip {
    private String id;
    private String busId;
    private String driverId;
    private String routeId;
    private Date startTime;
    private Date endTime;
    private String status;
    private int occupiedSeats;
    private int totalSeats;

    public Trip(String id, String busId, String driverId, String routeId, Date startTime, Date endTime, String status) {
        this.id = id;
        this.busId = busId;
        this.driverId = driverId;
        this.routeId = routeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getBusId() {
        return busId;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getRouteId() {
        return routeId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public int getOccupiedSeats() {
        return occupiedSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    @Override
    public String toString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        return sdf.format(startTime) + " - " + (endTime != null ? sdf.format(endTime) : "En curso");
    }
}
