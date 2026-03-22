// =============================================================================
// PayrollService.java
// Part of: Centralized Apparel Warehouse Management System (WMS)
// Module: Service Layer — Payroll Business Logic
//
// OOP CONCEPTS USED IN THIS CLASS:
// - ENCAPSULATION: All three repository dependencies are private final fields,
//   accessible only through the constructor. The core calculation logic is
//   hidden inside the private calculate() method — public methods expose only
//   what callers need, while the implementation details remain internal.
// - ABSTRACTION: PayrollRepository, EmployeeRepository, and AttendanceRepository
//   each abstract away all direct SQL/database operations. AppConfig abstracts
//   statutory rate constants. AuditLogger and SessionManager abstract audit
//   logging and session state. This service only expresses business rules.
// - POLYMORPHISM: The stream operations inside calculate() use method references
//   (AttendanceRecord::getOvertimeHours) and lambda filters that work uniformly
//   across any list of AttendanceRecord objects regardless of their origin.
// - INHERITANCE: Payroll, Employee, and AttendanceRecord are model objects
//   whose fields are accessed through getter/setter methods — a standard
//   JavaBean inheritance contract used consistently across the model layer.
// =============================================================================

package com.group100.wms.service;

import com.group100.wms.core.AppConfig;
import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.PayrollCalculationException;
import com.group100.wms.model.AttendanceRecord;
import com.group100.wms.model.Employee;
import com.group100.wms.model.Payroll;
import com.group100.wms.repository.AttendanceRepository;
import com.group100.wms.repository.EmployeeRepository;
import com.group100.wms.repository.PayrollRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PayrollService {

    // Repository for saving and retrieving Payroll records from the database
    private final PayrollRepository payrollRepository;

    // Repository for fetching Employee records, used to get daily rates and active status
    private final EmployeeRepository employeeRepository;

    // Repository for fetching AttendanceRecord data, used to determine worked days and overtime
    private final AttendanceRepository attendanceRepository;

    // Constructor that injects all three repository dependencies.
    // Follows the Dependency Injection pattern so repositories can be
    // swapped or mocked independently without changing this class.
    public PayrollService(PayrollRepository payrollRepository,
                          EmployeeRepository employeeRepository,
                          AttendanceRepository attendanceRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    // Generates payroll for all active employees for the given month and year.
    // Fetches attendance records per employee, calculates each payroll via calculate(),
    // persists each result to the database, and logs the batch action to the audit trail.
    // Returns the full list of generated Payroll objects.
    // Throws DatabaseException on repository failures or PayrollCalculationException
    // if any employee has an invalid daily rate.
    public List<Payroll> generatePayroll(int month, int year)
            throws DatabaseException, PayrollCalculationException {
        List<Employee> employees = employeeRepository.findAll();
        List<Payroll> results = new ArrayList<>();

        for (Employee emp : employees) {
            if (!emp.isActive()) continue;
            List<AttendanceRecord> records =
                    attendanceRepository.findByMonthYear(month, year);
            records.removeIf(r -> r.getEmployeeId() != emp.getId());
            Payroll payroll = calculate(emp, records, month, year);
            payrollRepository.save(payroll);
            results.add(payroll);
        }
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "GENERATE", "payroll", "BATCH",
                "Payroll generated for " + month + "/" + year);
        return results;
    }

    // Retrieves all previously generated payroll records for a specific month and year.
    // Delegates directly to the payroll repository and returns the result list.
    // Throws DatabaseException if the query fails.
    public List<Payroll> getPayrollByMonthYear(int month, int year)
            throws DatabaseException {
        return payrollRepository.findByMonthYear(month, year);
    }

    // Retrieves all payroll records associated with a specific employee ID.
    // Useful for viewing an individual employee's full payroll history.
    // Throws DatabaseException if the query fails.
    public List<Payroll> getPayrollByEmployee(int employeeId)
            throws DatabaseException {
        return payrollRepository.findByEmployee(employeeId);
    }

    // Core payroll calculation method for a single employee.
    // Counts PRESENT and HALF_DAY attendance records to derive total worked days,
    // sums overtime hours, then computes base salary, overtime pay, gross salary,
    // EPF (employee and employer portions), ETF, and net salary using rates from AppConfig.
    // All monetary values are rounded to two decimal places.
    // Stamps the generated Payroll with the current user's ID and the current timestamp.
    // Throws PayrollCalculationException if the employee's daily rate is zero or negative.
    private Payroll calculate(Employee emp, List<AttendanceRecord> records,
                              int month, int year)
            throws PayrollCalculationException {
        if (emp.getDailyRate() <= 0)
            throw new PayrollCalculationException(
                    "Invalid daily rate for employee: " + emp.getFullName());

        // Count of fully present days — each contributes 1.0 to worked days
        long presentDays = records.stream()
                .filter(r -> "PRESENT".equals(r.getStatus())).count();

        // Count of half days — each contributes 0.5 to worked days
        long halfDays = records.stream()
                .filter(r -> "HALF_DAY".equals(r.getStatus())).count();

        // Total effective days worked, combining full and half days
        double workedDays = presentDays + (halfDays * 0.5);

        // Sum of all overtime hours logged across the employee's attendance records
        double totalOvertime = records.stream()
                .mapToDouble(AttendanceRecord::getOvertimeHours).sum();

        // Base salary calculated from worked days multiplied by the employee's daily rate
        double baseSalary    = workedDays * emp.getDailyRate();

        // Overtime pay: hourly rate (daily rate / 8) multiplied by hours and the overtime multiplier
        double overtimePay   = totalOvertime * (emp.getDailyRate() / 8.0)
                * AppConfig.OVERTIME_RATE;

        // Gross salary before any statutory deductions
        double grossSalary   = baseSalary + overtimePay;

        // Employee's EPF contribution deducted from their gross salary
        double epfEmployee   = Math.round(grossSalary * AppConfig.EPF_EMPLOYEE_RATE * 100.0) / 100.0;

        // Employer's EPF contribution (an additional cost borne by the company, not deducted from employee)
        double epfEmployer   = Math.round(grossSalary * AppConfig.EPF_EMPLOYER_RATE * 100.0) / 100.0;

        // ETF (Employees' Trust Fund) contribution paid by the employer
        double etf           = Math.round(grossSalary * AppConfig.ETF_RATE * 100.0) / 100.0;

        // Net salary received by the employee after the EPF employee deduction
        double netSalary     = Math.round((grossSalary - epfEmployee) * 100.0) / 100.0;

        Payroll payroll = new Payroll();
        payroll.setEmployeeId(emp.getId());
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setBaseSalary(baseSalary);
        payroll.setOvertimePay(Math.round(overtimePay * 100.0) / 100.0);
        payroll.setEpfEmployee(epfEmployee);
        payroll.setEpfEmployer(epfEmployer);
        payroll.setEtf(etf);
        payroll.setNetSalary(netSalary);
        payroll.setGeneratedBy(SessionManager.getCurrentUser().getId());
        payroll.setGeneratedAt(LocalDateTime.now());
        return payroll;
    }
}