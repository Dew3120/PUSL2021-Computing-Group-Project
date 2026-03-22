package com.group100.wms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a leave request submitted by an employee, including details
 * such as leave type, reason, dates, status, and approval information.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access and modification are controlled 
 *   exclusively through public getter and setter methods
 * - Abstraction: Provides a clean, high-level interface for managing leave request 
 *   data without exposing internal representation or persistence details
 */
public class LeaveRequest {
    
    // Unique identifier for this leave request in the database
    private int requestId;
    
    // Foreign key linking this request to the Employee who is requesting leave
    private int employeeId;
    
    // Cached/denormalized name of the requesting employee (for display/UI/reporting)
    private String employeeName;
    
    // Department, section, or team the employee belongs to
    private String section;
    
    // Date when the leave request was submitted
    private LocalDate requestDate;
    
    // Type of leave being requested (e.g., "Annual", "Sick", "Casual", "Maternity", "Unpaid")
    private String leaveType;
    
    // Reason or justification provided by the employee for the leave
    private String reason;
    
    // Current status of the leave request (e.g., "PENDING", "APPROVED", "REJECTED", "CANCELLED")
    private String status;
    
    // ID of the User (usually a manager or HR) who created/submitted this request on behalf of the employee
    private int createdBy;
    
    // Timestamp when this leave request was created/submitted
    private LocalDateTime createdAt;
    
    // Timestamp when this request was reviewed/approved/rejected by an authority
    private LocalDateTime reviewedAt;

    // Default constructor - useful for creating empty leave request objects or for frameworks
    public LeaveRequest() {}

    /**
     * Gets the unique identifier of this leave request
     * @return request ID
     */
    public int getRequestId() { return requestId; }

    /**
     * Gets the ID of the employee requesting the leave
     * @return employee ID
     */
    public int getEmployeeId() { return employeeId; }

    /**
     * Gets the cached name of the requesting employee
     * @return employee name (may be null)
     */
    public String getEmployeeName() { return employeeName; }

    /**
     * Gets the section/department of the employee
     * @return section name
     */
    public String getSection() { return section; }

    /**
     * Gets the date when the leave request was submitted
     * @return request submission date
     */
    public LocalDate getRequestDate() { return requestDate; }

    /**
     * Gets the type/category of leave requested
     * @return leave type
     */
    public String getLeaveType() { return leaveType; }

    /**
     * Gets the reason provided for the leave
     * @return leave reason
     */
    public String getReason() { return reason; }

    /**
     * Gets the current status of the leave request
     * @return status string
     */
    public String getStatus() { return status; }

    /**
     * Gets the ID of the user who created/submitted this request
     * @return creator user ID
     */
    public int getCreatedBy() { return createdBy; }

    /**
     * Gets the timestamp when this request was created
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Gets the timestamp when this request was reviewed
     * @return review timestamp (may be null if not yet reviewed)
     */
    public LocalDateTime getReviewedAt() { return reviewedAt; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param v request ID to set
     */
    public void setRequestId(int v) { this.requestId = v; }

    /**
     * Sets or updates the requesting employee
     * @param v employee ID to set
     */
    public void setEmployeeId(int v) { this.employeeId = v; }

    /**
     * Sets or updates the cached employee name
     * @param v employee name to set
     */
    public void setEmployeeName(String v) { this.employeeName = v; }

    /**
     * Sets or updates the employee's section/department
     * @param v section name to set
     */
    public void setSection(String v) { this.section = v; }

    /**
     * Sets or updates the date the request was submitted
     * @param v request date to set
     */
    public void setRequestDate(LocalDate v) { this.requestDate = v; }

    /**
     * Sets or updates the type of leave
     * @param v leave type to set
     */
    public void setLeaveType(String v) { this.leaveType = v; }

    /**
     * Sets or updates the reason for the leave
     * @param v reason to set
     */
    public void setReason(String v) { this.reason = v; }

    /**
     * Sets or updates the status of the leave request
     * @param v status to set (e.g., "APPROVED", "REJECTED")
     */
    public void setStatus(String v) { this.status = v; }

    /**
     * Sets or updates the user who created this request
     * @param v creator user ID to set
     */
    public void setCreatedBy(int v) { this.createdBy = v; }

    /**
     * Sets or updates the creation timestamp
     * @param v creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    /**
     * Sets or updates the review timestamp
     * @param v review timestamp to set
     */
    public void setReviewedAt(LocalDateTime v) { this.reviewedAt = v; }
}
