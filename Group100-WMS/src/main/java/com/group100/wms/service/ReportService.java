package com.group100.wms.service;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.AttendanceRecord;
import com.group100.wms.model.Payroll;
import com.group100.wms.repository.AttendanceRepository;
import com.group100.wms.repository.PayrollRepository;
import com.group100.wms.util.ExcelExporter;
import com.group100.wms.util.PdfExporter;

import java.util.List;

// OOP Concepts used in this class:
// 1. Encapsulation: The class bundles multiple repository dependencies and keeps them private, controlling how data is accessed for reporting.
// 2. Abstraction: It provides a high-level reporting interface that hides the complexity of SQL data retrieval and the specific implementation details of PDF/Excel generation.
public class ReportService {

    // Stores the repository used to fetch payroll data from the database
    private final PayrollRepository payrollRepository;
    // Stores the repository used to fetch attendance data from the database
    private final AttendanceRepository attendanceRepository;

    // Constructor to inject the required repository dependencies for the service
    public ReportService(PayrollRepository payrollRepository,
                         AttendanceRepository attendanceRepository) {
        this.payrollRepository = payrollRepository;
        this.attendanceRepository = attendanceRepository;
    }

    // Fetches payroll records for a specific month/year and generates a PDF document
    public void exportPayrollPdf(int month, int year, String outputPath)
            throws DatabaseException {
        // Stores the list of payroll objects retrieved for the specified period
        List<Payroll> payrolls = payrollRepository.findByMonthYear(month, year);
        PdfExporter.exportPayroll(payrolls, month, year, outputPath);
    }

    // Fetches payroll records for a specific month/year and generates an Excel spreadsheet
    public void exportPayrollExcel(int month, int year, String outputPath)
            throws DatabaseException {
        // Stores the list of payroll objects retrieved for the specified period
        List<Payroll> payrolls = payrollRepository.findByMonthYear(month, year);
        ExcelExporter.exportPayroll(payrolls, month, year, outputPath);
    }

    // Fetches attendance records for a specific month/year and generates a PDF document
    public void exportAttendancePdf(int month, int year, String outputPath)
            throws DatabaseException {
        // Stores the list of attendance records retrieved for the specified period
        List<AttendanceRecord> records = attendanceRepository.findByMonthYear(month, year);
        PdfExporter.exportAttendance(records, month, year, outputPath);
    }

    // Fetches attendance records for a specific month/year and generates an Excel spreadsheet
    public void exportAttendanceExcel(int month, int year, String outputPath)
            throws DatabaseException {
        // Stores the list of attendance records retrieved for the specified period
        List<AttendanceRecord> records = attendanceRepository.findByMonthYear(month, year);
        ExcelExporter.exportAttendance(records, month, year, outputPath);
    }
}
