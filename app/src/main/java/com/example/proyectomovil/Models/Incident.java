package com.example.proyectomovil.Models;

import java.util.Date;

public class Incident {
    private String Id;
    private String TripId;
    private String DriverId;
    private String Type;
    private Date ReportedAt;

    public Incident(String id, String tripId, String driverId, String type, Date reportedAt) {
        Id = id;
        TripId = tripId;
        DriverId = driverId;
        Type = type;
        ReportedAt = reportedAt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTripId() {
        return TripId;
    }

    public void setTripId(String tripId) {
        TripId = tripId;
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Date getReportedAt() {
        return ReportedAt;
    }

    public void setReportedAt(Date reportedAt) {
        ReportedAt = reportedAt;
    }
}
