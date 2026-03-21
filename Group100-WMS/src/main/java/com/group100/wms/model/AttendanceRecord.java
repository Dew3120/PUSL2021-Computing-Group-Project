package com.group100.wms.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class AttendanceRecord {
    private int id;
    private int employeeId;
    private LocalDate date;
    private LocalTime clockIn;
    private LocalTime clockOut;
    private String status;
    private int approvedBy;
    private String employeeName;

    public AttendanceRecord() {}

    public AttendanceRecord(int id, int employeeId, LocalDate date,
                            LocalTime clockIn, LocalTime clockOut,
                            String status, int approvedBy) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
        this.status = status;
        this.approvedBy = approvedBy;
    }

    public int getId()              { return id; }
    public int getEmployeeId()      { return employeeId; }
    public LocalDate getDate()      { return date; }
    public LocalTime getClockIn()   { return clockIn; }
    public LocalTime getClockOut()  { return clockOut; }
    public String getStatus()       { return status; }
    public int getApprovedBy()      { return approvedBy; }
    public String getEmployeeName() { return employeeName; }

    public double getHoursWorked() {
        if (clockIn == null || clockOut == null) return 0;
        return ChronoUnit.MINUTES.between(clockIn, clockOut) / 60.0;
    }

    public double getOvertimeHours() {
        double worked = getHoursWorked();
        return worked > 8.0 ? worked - 8.0 : 0.0;
    }

    public void setId(int id)                  { this.id = id; }
    public void setEmployeeId(int employeeId)  { this.employeeId = employeeId; }
    public void setDate(LocalDate date)        { this.date = date; }
    public void setClockIn(LocalTime clockIn)  { this.clockIn = clockIn; }
    public void setClockOut(LocalTime clockOut){ this.clockOut = clockOut; }
    public void setStatus(String status)       { this.status = status; }
    public void setApprovedBy(int approvedBy)  { this.approvedBy = approvedBy; }
    public void setEmployeeName(String n)      { this.employeeName = n; }

    @Override
    public String toString() {
        return "AttendanceRecord{id=" + id + ", employeeId=" + employeeId
                + ", date=" + date + ", status='" + status + "'}";
    }
}