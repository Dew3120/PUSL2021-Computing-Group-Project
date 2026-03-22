package com.group100.wms.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a single day's attendance record for an employee,
 * including clock-in/out times, calculated hours, overtime, status, and approval information.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access is controlled through public getters and setters
 * - Abstraction: Exposes a clean interface for attendance data and provides calculated properties 
 *   (hours worked, overtime) without exposing the underlying time arithmetic logic
 */
public class AttendanceRecord {
    
    // Unique identifier for this attendance record in the database
    private int id;
    
    // Foreign key linking this record to the corresponding Employee
    private int employeeId;
    
    // Date of this attendance record (the workday being recorded)
    private LocalDate date;
    
    // Time when the employee clocked in (started work)
    private LocalTime clockIn;
    
    // Time when the employee clocked out (ended work)
    private LocalTime clockOut;
    
    // Status of the attendance record (e.g., "PRESENT", "ABSENT", "LATE", "HALF-DAY", "APPROVED", "PENDING")
    private String status;
    
    // ID of the User/Employee (usually a manager/supervisor) who approved this record
    private int approvedBy;
    
    // Cached/denormalized name of the employee (for display/UI/reporting convenience)
    private String employeeName;

    // Default constructor - useful for creating empty attendance objects or for frameworks
    public AttendanceRecord() {}

    /**
     * Parameterized constructor to create a fully initialized AttendanceRecord object
     * @param id unique record identifier
     * @param employeeId linked employee ID
     * @param date date of attendance
     * @param clockIn clock-in time
     * @param clockOut clock-out time
     * @param status attendance status
     * @param approvedBy ID of the approving user
     */
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

    /**
     * Gets the unique identifier of this attendance record
     * @return record ID
     */
    public int getId() { return id; }

    /**
     * Gets the ID of the employee this record belongs to
     * @return employee ID
     */
    public int getEmployeeId() { return employeeId; }

    /**
     * Gets the date this attendance record represents
     * @return attendance date
     */
    public LocalDate getDate() { return date; }

    /**
     * Gets the clock-in time for this day
     * @return clock-in time (may be null if not recorded)
     */
    public LocalTime getClockIn() { return clockIn; }

    /**
     * Gets the clock-out time for this day
     * @return clock-out time (may be null if not recorded)
     */
    public LocalTime getClockOut() { return clockOut; }

    /**
     * Gets the current status of this attendance record
     * @return status string
     */
    public String getStatus() { return status; }

    /**
     * Gets the ID of the user who approved this attendance record
     * @return approver's user ID
     */
    public int getApprovedBy() { return approvedBy; }

    /**
     * Gets the cached employee name for display purposes
     * @return employee name (may be null)
     */
    public String getEmployeeName() { return employeeName; }

    /**
     * Calculates and returns the total hours worked on this day.
     * Returns 0 if clock-in or clock-out is missing.
     * @return hours worked (as a decimal, e.g., 7.5)
     */
    public double getHoursWorked() {
        if (clockIn == null || clockOut == null) return 0;
        return ChronoUnit.MINUTES.between(clockIn, clockOut) / 60.0;
    }

    /**
     * Calculates and returns overtime hours for this day.
     * Assumes standard 8-hour workday; returns 0 if ≤ 8 hours worked.
     * @return overtime hours (as a decimal, e.g., 2.0)
     */
    public double getOvertimeHours() {
        double worked = getHoursWorked();
        return worked > 8.0 ? worked - 8.0 : 0.0;
    }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the linked employee
     * @param employeeId employee ID to set
     */
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    /**
     * Sets or updates the attendance date
     * @param date date to set
     */
    public void setDate(LocalDate date) { this.date = date; }

    /**
     * Sets or updates the clock-in time
     * @param clockIn clock-in time to set
     */
    public void setClockIn(LocalTime clockIn) { this.clockIn = clockIn; }

    /**
     * Sets or updates the clock-out time
     * @param clockOut clock-out time to set
     */
    public void setClockOut(LocalTime clockOut){ this.clockOut = clockOut; }

    /**
     * Sets or updates the attendance status
     * @param status status to set
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Sets or updates the approving user
     * @param approvedBy approver user ID to set
     */
    public void setApprovedBy(int approvedBy) { this.approvedBy = approvedBy; }

    /**
     * Sets or updates the cached employee name
     * @param n employee name to set
     */
    public void setEmployeeName(String n) { this.employeeName = n; }

    /**
     * Returns a string representation of the AttendanceRecord object 
     * (useful for logging/debugging)
     * @return string containing id, employeeId, date, and status
     */
    @Override
    public String toString() {
        return "AttendanceRecord{id=" + id + ", employeeId=" + employeeId
                + ", date=" + date + ", status='" + status + "'}";
    }
}
