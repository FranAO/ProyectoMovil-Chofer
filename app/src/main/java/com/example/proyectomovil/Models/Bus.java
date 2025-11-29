package com.example.proyectomovil.Models;

public class Bus {
    private String Id;
    private String BusCode;
    private String Plate;
    private int Capacity;
    private String Status;
    private String DriverId;
    private String RouteId;

    public Bus(String id, String busCode, String plate, int capacity, String status, String driverId, String routeId) {
        Id = id;
        BusCode = busCode;
        Plate = plate;
        Capacity = capacity;
        Status = status;
        DriverId = driverId;
        RouteId = routeId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getBusCode() {
        return BusCode;
    }

    public void setBusCode(String busCode) {
        BusCode = busCode;
    }

    public String getPlate() {
        return Plate;
    }

    public void setPlate(String plate) {
        Plate = plate;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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
}
