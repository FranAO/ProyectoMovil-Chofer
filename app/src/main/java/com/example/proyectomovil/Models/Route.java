package com.example.proyectomovil.Models;

import java.util.List;

public class Route {
    private String Id;
    private String RouteName;
    private List<String> StopIds;
    private List<String> ScheduleIds;
    private Double Price;
    private int EstimatedDuration;

    public Route(String id, String routeName, List<String> stopIds, List<String> scheduleIds, Double price, int estimatedDuration) {
        Id = id;
        RouteName = routeName;
        StopIds = stopIds;
        ScheduleIds = scheduleIds;
        Price = price;
        EstimatedDuration = estimatedDuration;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getRouteName() {
        return RouteName;
    }

    public void setRouteName(String routeName) {
        RouteName = routeName;
    }

    public List<String> getStopIds() {
        return StopIds;
    }

    public void setStopIds(List<String> stopIds) {
        StopIds = stopIds;
    }

    public List<String> getScheduleIds() {
        return ScheduleIds;
    }

    public void setScheduleIds(List<String> scheduleIds) {
        ScheduleIds = scheduleIds;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public int getEstimatedDuration() {
        return EstimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        EstimatedDuration = estimatedDuration;
    }
}
