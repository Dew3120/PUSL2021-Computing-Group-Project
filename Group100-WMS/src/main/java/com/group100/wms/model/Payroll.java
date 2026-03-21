package com.group100.wms.model;

import java.time.LocalDateTime;

public class Payroll {
    private int id;
    private int employeeId;
    private int month;
    private int year;
    private double baseSalary;
    private double overtimePay;
    private double epfEmployee;
    private double epfEmployer;
    private double etf;
    private double netSalary;
    private int generatedBy;
    private LocalDateTime generatedAt;
    private String employeeName;

    public Payroll() {}

    public int getId()                      { return id; }
    public int getEmployeeId()              { return employeeId; }
    public int getMonth()                   { return month; }
    public int getYear()                    { return year; }
    public double getBaseSalary()           { return baseSalary; }
    public double getOvertimePay()          { return overtimePay; }
    public double getEpfEmployee()          { return epfEmployee; }
    public double getEpfEmployer()          { return epfEmployer; }
    public double getEtf()                  { return etf; }
    public double getNetSalary()            { return netSalary; }
    public int getGeneratedBy()             { return generatedBy; }
    public LocalDateTime getGeneratedAt()   { return generatedAt; }
    public String getEmployeeName()         { return employeeName; }

    public void setId(int id)                          { this.id = id; }
    public void setEmployeeId(int employeeId)          { this.employeeId = employeeId; }
    public void setMonth(int month)                    { this.month = month; }
    public void setYear(int year)                      { this.year = year; }
    public void setBaseSalary(double baseSalary)       { this.baseSalary = baseSalary; }
    public void setOvertimePay(double overtimePay)     { this.overtimePay = overtimePay; }
    public void setEpfEmployee(double epfEmployee)     { this.epfEmployee = epfEmployee; }
    public void setEpfEmployer(double epfEmployer)     { this.epfEmployer = epfEmployer; }
    public void setEtf(double etf)                     { this.etf = etf; }
    public void setNetSalary(double netSalary)         { this.netSalary = netSalary; }
    public void setGeneratedBy(int generatedBy)        { this.generatedBy = generatedBy; }
    public void setGeneratedAt(LocalDateTime g)        { this.generatedAt = g; }
    public void setEmployeeName(String n)              { this.employeeName = n; }

    @Override
    public String toString() {
        return "Payroll{id=" + id + ", employeeId=" + employeeId
                + ", month=" + month + ", year=" + year
                + ", netSalary=" + netSalary + "}";
    }
}