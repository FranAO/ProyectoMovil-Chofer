package com.example.proyectomovil.Models;

import java.time.Duration;
import java.util.List;

public class Schedule {
    private Duration DepartureTime;
    private Duration ArrivalTime;
    private List<String> OperationsDays;

    public Schedule(Duration departureTime, Duration arrivalTime, List<String> operationsDays) {
        DepartureTime = departureTime;
        ArrivalTime = arrivalTime;
        OperationsDays = operationsDays;
    }

    public Duration getDepartureTime() {
        return DepartureTime;
    }

    public void setDepartureTime(Duration departureTime) {
        DepartureTime = departureTime;
    }

    public Duration getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(Duration arrivalTime) {
        ArrivalTime = arrivalTime;
    }

    public List<String> getOperationsDays() {
        return OperationsDays;
    }

    public void setOperationsDays(List<String> operationsDays) {
        OperationsDays = operationsDays;
    }
}
