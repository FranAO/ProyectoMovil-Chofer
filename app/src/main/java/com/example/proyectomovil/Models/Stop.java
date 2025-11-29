package com.example.proyectomovil.Models;

public class Stop {
    private String Id;
    private String Name;
    private Double Lat;
    private Double Lng;

    public Stop(String id, String name, Double lat, Double lng) {
        Id = id;
        Name = name;
        Lat = lat;
        Lng = lng;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }
}
