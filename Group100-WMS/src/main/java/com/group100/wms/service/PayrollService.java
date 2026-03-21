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

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public PayrollService(PayrollRepository payrollRepository,
                          EmployeeRepository employeeRepository,
                          AttendanceRepository attendanceRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

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

    public List<Payroll> getPayrollByMonthYear(int month, int year)
            throws DatabaseException {
        return payrollRepository.findByMonthYear(month, year);
    }

    public List<Payroll> getPayrollByEmployee(int employeeId)
            throws DatabaseException {
        return payrollRepository.findByEmployee(employeeId);
    }

    private Payroll calculate(Employee emp, List<AttendanceRecord> records,
                              int month, int year)
            throws PayrollCalculationException {
        if (emp.getDailyRate() <= 0)
            throw new PayrollCalculationException(
                    "Invalid daily rate for employee: " + emp.getFullName());

        long presentDays = records.stream()
                .filter(r -> "PRESENT".equals(r.getStatus())).count();
        long halfDays = records.stream()
                .filter(r -> "HALF_DAY".equals(r.getStatus())).count();
        double workedDays = presentDays + (halfDays * 0.5);

        double totalOvertime = records.stream()
                .mapToDouble(AttendanceRecord::getOvertimeHours).sum();

        double baseSalary    = workedDays * emp.getDailyRate();
        double overtimePay   = totalOvertime * (emp.getDailyRate() / 8.0)
                * AppConfig.OVERTIME_RATE;
        double grossSalary   = baseSalary + overtimePay;
        double epfEmployee   = Math.round(grossSalary * AppConfig.EPF_EMPLOYEE_RATE * 100.0) / 100.0;
        double epfEmployer   = Math.round(grossSalary * AppConfig.EPF_EMPLOYER_RATE * 100.0) / 100.0;
        double etf           = Math.round(grossSalary * AppConfig.ETF_RATE * 100.0) / 100.0;
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