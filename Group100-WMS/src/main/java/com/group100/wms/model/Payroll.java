package com.group100.wms.model;

import java.time.LocalDateTime;

/**
 * Represents a monthly payroll record for an employee, including salary components,
 * deductions, contributions (EPF/ETF), and generation metadata.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access and modification are strictly controlled 
 *   through public getter and setter methods
 * - Abstraction: Provides a clean, high-level interface for payroll data without exposing 
 *   internal calculation or storage details
 */
public class Payroll {
    
    // Unique identifier for this payroll record in the database
    private int id;
    
    // Foreign key linking this payroll to the corresponding Employee
    private int employeeId;
    
    // Month of the payroll period (1 = January, 2 = February, ..., 12 = December)
    private int month;
    
    // Year of the payroll period
    private int year;
    
    // Base salary amount before any additions or deductions
    private double baseSalary;
    
    // Additional pay earned from overtime hours during the period
    private double overtimePay;
    
    // Employee's contribution to the Employees' Provident Fund (EPF)
    private double epfEmployee;
    
    // Employer's contribution to the Employees' Provident Fund (EPF)
    private double epfEmployer;
    
    // Employer's contribution to the Employees' Trust Fund (ETF)
    private double etf;
    
    // Final take-home salary after all additions and deductions
    private double netSalary;
    
    // ID of the User (usually an accountant or admin) who generated this payroll record
    private int generatedBy;
    
    // Timestamp when this payroll record was generated/calculated
    private LocalDateTime generatedAt;
    
    // Cached/denormalized name of the employee (for display/UI/reporting convenience)
    private String employeeName;

    // Default constructor - useful for creating empty payroll objects or for frameworks
    public Payroll() {}

    /**
     * Gets the unique identifier of this payroll record
     * @return payroll record ID
     */
    public int getId() { return id; }

    /**
     * Gets the ID of the employee this payroll belongs to
     * @return employee ID
     */
    public int getEmployeeId() { return employeeId; }

    /**
     * Gets the month of the payroll period
     * @return month number (1-12)
     */
    public int getMonth() { return month; }

    /**
     * Gets the year of the payroll period
     * @return year
     */
    public int getYear() { return year; }

    /**
     * Gets the base salary amount for this period
     * @return base salary
     */
    public double getBaseSalary() { return baseSalary; }

    /**
     * Gets the overtime pay amount for this period
     * @return overtime pay
     */
    public double getOvertimePay() { return overtimePay; }

    /**
     * Gets the employee's EPF contribution amount
     * @return employee EPF contribution
     */
    public double getEpfEmployee() { return epfEmployee; }

    /**
     * Gets the employer's EPF contribution amount
     * @return employer EPF contribution
     */
    public double getEpfEmployer() { return epfEmployer; }

    /**
     * Gets the employer's ETF contribution amount
     * @return ETF contribution
     */
    public double getEtf() { return etf; }

    /**
     * Gets the net (take-home) salary after all calculations
     * @return net salary
     */
    public double getNetSalary() { return netSalary; }

    /**
     * Gets the ID of the user who generated this payroll
     * @return generator user ID
     */
    public int getGeneratedBy() { return generatedBy; }

    /**
     * Gets the timestamp when this payroll was generated
     * @return generation timestamp
     */
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    /**
     * Gets the cached employee name for display purposes
     * @return employee name (may be null)
     */
    public String getEmployeeName() { return employeeName; }

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
     * Sets or updates the payroll month
     * @param month month number (1-12) to set
     */
    public void setMonth(int month) { this.month = month; }

    /**
     * Sets or updates the payroll year
     * @param year year to set
     */
    public void setYear(int year) { this.year = year; }

    /**
     * Sets or updates the base salary amount
     * @param baseSalary base salary to set
     */
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }

    /**
     * Sets or updates the overtime pay amount
     * @param overtimePay overtime pay to set
     */
    public void setOvertimePay(double overtimePay) { this.overtimePay = overtimePay; }

    /**
     * Sets or updates the employee's EPF contribution
     * @param epfEmployee EPF employee contribution to set
     */
    public void setEpfEmployee(double epfEmployee) { this.epfEmployee = epfEmployee; }

    /**
     * Sets or updates the employer's EPF contribution
     * @param epfEmployer EPF employer contribution to set
     */
    public void setEpfEmployer(double epfEmployer) { this.epfEmployer = epfEmployer; }

    /**
     * Sets or updates the ETF contribution amount
     * @param etf ETF amount to set
     */
    public void setEtf(double etf) { this.etf = etf; }

    /**
     * Sets or updates the final net salary
     * @param netSalary net salary to set
     */
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }

    /**
     * Sets or updates the user who generated this payroll
     * @param generatedBy generator user ID to set
     */
    public void setGeneratedBy(int generatedBy) { this.generatedBy = generatedBy; }

    /**
     * Sets or updates the timestamp when payroll was generated
     * @param g generation timestamp to set
     */
    public void setGeneratedAt(LocalDateTime g) { this.generatedAt = g; }

    /**
     * Sets or updates the cached employee name
     * @param n employee name to set
     */
    public void setEmployeeName(String n) { this.employeeName = n; }

    /**
     * Returns a string representation of the Payroll object (useful for logging/debugging)
     * @return string containing id, employeeId, month, year, and netSalary
     */
    @Override
    public String toString() {
        return "Payroll{id=" + id + ", employeeId=" + employeeId
                + ", month=" + month + ", year=" + year
                + ", netSalary=" + netSalary + "}";
    }
}
