package com.example.proyectomovil.Models;

import java.util.Date;
import java.util.List;

public class Trip {
    private String Id;
    private String DriverId;
    private String RouteId;
    private Date StartTime;
    private Date EndTime;
    private String Status;
    private int OccupiedSeats;
    private int TotalSeats;
    private List<String> PassengerIds;
    private List<String> TicketsIds;

    public Trip(String id, String driverId, String routeId, Date startTime, Date endTime, String status, int occupiedSeats, int totalSeats, List<String> passengerIds, List<String> ticketsIds) {
        Id = id;
        DriverId = driverId;
        RouteId = routeId;
        StartTime = startTime;
        EndTime = endTime;
        Status = status;
        OccupiedSeats = occupiedSeats;
        TotalSeats = totalSeats;
        PassengerIds = passengerIds;
        TicketsIds = ticketsIds;
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

    public String getRouteId() {
        return RouteId;
    }

    public void setRouteId(String routeId) {
        RouteId = routeId;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getOccupiedSeats() {
        return OccupiedSeats;
    }

    public void setOccupiedSeats(int occupiedSeats) {
        OccupiedSeats = occupiedSeats;
    }

    public int getTotalSeats() {
        return TotalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        TotalSeats = totalSeats;
    }

    public List<String> getPassengerIds() {
        return PassengerIds;
    }

    public void setPassengerIds(List<String> passengerIds) {
        PassengerIds = passengerIds;
    }

    public List<String> getTicketsIds() {
        return TicketsIds;
    }

    public void setTicketsIds(List<String> ticketsIds) {
        TicketsIds = ticketsIds;
    }
}
