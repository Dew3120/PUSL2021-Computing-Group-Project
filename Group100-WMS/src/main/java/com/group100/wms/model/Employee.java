package com.group100.wms.model;

/**
 * Represents an employee in the warehouse management system,
 * typically linked to a system User account.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; external access is strictly controlled via public getter and setter methods
 * - Abstraction: The class provides a clean, high-level interface for employee data without exposing internal storage details
 */
public class Employee {
    
    // Unique identifier for the employee record in the database
    private int id;
    
    // Foreign key linking this employee to a corresponding User account (for login/permissions)
    private int userId;
    
    // Full legal or official name of the employee
    private String fullName;
    
    // Job title or position of the employee (e.g., "Warehouse Worker", "Supervisor", "Driver")
    private String designation;
    
    // Daily wage or rate paid to this employee
    private double dailyRate;
    
    // Indicates whether the employee is currently active/employed
    private boolean isActive;
    
    // Alternative or display name for the employee (possibly used in UI/reports)
    private String employeeName;

    // Default constructor - useful when creating empty objects or for frameworks
    public Employee() {}

    /**
     * Parameterized constructor to create a fully initialized Employee object
     * @param id unique employee identifier
     * @param userId linked user account ID
     * @param fullName employee's full name
     * @param designation employee's job title/position
     * @param dailyRate daily payment rate
     * @param isActive whether the employee is currently active
     */
    public Employee(int id, int userId, String fullName,
                    String designation, double dailyRate, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.designation = designation;
        this.dailyRate = dailyRate;
        this.isActive = isActive;
    }

    /**
     * Gets the unique identifier of this employee
     * @return the employee ID
     */
    public int getId() { return id; }

    /**
     * Gets the ID of the linked user account
     * @return the associated user ID
     */
    public int getUserId() { return userId; }

    /**
     * Gets the employee's full name
     * @return full name
     */
    public String getFullName() { return fullName; }

    /**
     * Gets the employee's job title or designation
     * @return designation
     */
    public String getDesignation() { return designation; }

    /**
     * Gets the daily wage/rate for this employee
     * @return daily rate
     */
    public double getDailyRate() { return dailyRate; }

    /**
     * Checks if the employee is currently active
     * @return true if active, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Gets the alternative/display name for the employee
     * @return employee name (may be null or same as fullName in some cases)
     */
    public String getEmployeeName() { return employeeName; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Links or updates the associated user account
     * @param userId the user ID to set
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Sets or updates the employee's full name
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * Sets or updates the employee's designation/job title
     * @param d the designation to set
     */
    public void setDesignation(String d) { this.designation = d; }

    /**
     * Sets or updates the daily rate/wage
     * @param dailyRate the daily rate to set
     */
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }

    /**
     * Sets the active status of the employee
     * @param active true to mark as active, false to mark as inactive
     */
    public void setActive(boolean active) { this.isActive = active; }

    /**
     * Sets or updates the alternative/display name
     * @param n the employee name to set
     */
    public void setEmployeeName(String n) { this.employeeName = n; }

    /**
     * Returns a string representation of the Employee object (useful for logging/debugging)
     * @return string containing id, fullName, and designation
     */
    @Override
    public String toString() {
        return "Employee{id=" + id + ", fullName='" + fullName
                + "', designation='" + designation + "'}";
    }
}
