package com.group100.wms.model;

public class Employee {
    private int id;
    private int userId;
    private String fullName;
    private String designation;
    private double dailyRate;
    private boolean isActive;
    private String employeeName;

    public Employee() {}

    public Employee(int id, int userId, String fullName,
                    String designation, double dailyRate, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.designation = designation;
        this.dailyRate = dailyRate;
        this.isActive = isActive;
    }

    public int getId()              { return id; }
    public int getUserId()          { return userId; }
    public String getFullName()     { return fullName; }
    public String getDesignation()  { return designation; }
    public double getDailyRate()    { return dailyRate; }
    public boolean isActive()       { return isActive; }
    public String getEmployeeName() { return employeeName; }

    public void setId(int id)                  { this.id = id; }
    public void setUserId(int userId)          { this.userId = userId; }
    public void setFullName(String fullName)   { this.fullName = fullName; }
    public void setDesignation(String d)       { this.designation = d; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }
    public void setActive(boolean active)      { this.isActive = active; }
    public void setEmployeeName(String n)      { this.employeeName = n; }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", fullName='" + fullName
                + "', designation='" + designation + "'}";
    }
}