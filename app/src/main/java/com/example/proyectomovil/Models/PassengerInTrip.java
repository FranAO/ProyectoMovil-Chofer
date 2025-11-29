package com.example.proyectomovil.Models;

import java.util.Date;

public class PassengerInTrip {
    private String Id;
    private String StudentId;
    private String TicketId;
    private Date BoardedAt;
    private boolean ValidatedByDriver;

    public PassengerInTrip(String id, String studentId, String ticketId, Date boardedAt, boolean validatedByDriver) {
        Id = id;
        StudentId = studentId;
        TicketId = ticketId;
        BoardedAt = boardedAt;
        ValidatedByDriver = validatedByDriver;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getTicketId() {
        return TicketId;
    }

    public void setTicketId(String ticketId) {
        TicketId = ticketId;
    }

    public Date getBoardedAt() {
        return BoardedAt;
    }

    public void setBoardedAt(Date boardedAt) {
        BoardedAt = boardedAt;
    }

    public boolean isValidatedByDriver() {
        return ValidatedByDriver;
    }

    public void setValidatedByDriver(boolean validatedByDriver) {
        ValidatedByDriver = validatedByDriver;
    }
}
